package com.clinica.api.service;

import com.clinica.api.dto.ConsultaRequest;
import com.clinica.api.dto.ConsultaResponse;
import com.clinica.api.exception.ConflitoException;
import com.clinica.api.exception.RecursoNaoEncontradoException;
import com.clinica.api.exception.RegraNegocioException;
import com.clinica.api.model.Consulta;
import com.clinica.api.model.Medico;
import com.clinica.api.model.Paciente;
import com.clinica.api.model.StatusConsulta;
import com.clinica.api.repository.ConsultaRepository;
import com.clinica.api.repository.MedicoRepository;
import com.clinica.api.repository.PacienteRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.clinica.api.model.StatusConsulta.*;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    /** Duração padrão de cada consulta, usada para detectar conflitos de agenda. */
    static final int DURACAO_MINUTOS = 30;

    /** Status que liberam o horário na agenda do médico. */
    private static final List<StatusConsulta> LIBERAM_HORARIO = List.of(CANCELADA, FALTOU);

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional
    public ConsultaResponse agendar(ConsultaRequest r) {
        Medico medico = medicoRepository.findById(r.medicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado: " + r.medicoId()));
        if (!medico.isAtivo()) {
            throw new RegraNegocioException("O médico informado está inativo");
        }
        Paciente paciente = pacienteRepository.findById(r.pacienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado: " + r.pacienteId()));

        boolean conflito = consultaRepository.existeConflito(
                medico.getId(),
                r.dataHora().minusMinutes(DURACAO_MINUTOS),
                r.dataHora().plusMinutes(DURACAO_MINUTOS),
                LIBERAM_HORARIO);
        if (conflito) {
            throw new ConflitoException("O médico já possui consulta neste horário");
        }

        Consulta consulta = Consulta.builder()
                .medico(medico)
                .paciente(paciente)
                .dataHora(r.dataHora())
                .observacoes(r.observacoes())
                .status(AGENDADA)
                .build();
        return ConsultaResponse.de(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscar(Long id) {
        return ConsultaResponse.de(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listar(Long medicoId, Long pacienteId, StatusConsulta status,
                                         LocalDate de, LocalDate ate, Pageable pageable) {
        Specification<Consulta> spec = (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (medicoId != null) filtros.add(cb.equal(root.get("medico").get("id"), medicoId));
            if (pacienteId != null) filtros.add(cb.equal(root.get("paciente").get("id"), pacienteId));
            if (status != null) filtros.add(cb.equal(root.get("status"), status));
            if (de != null) filtros.add(cb.greaterThanOrEqualTo(root.get("dataHora"), de.atStartOfDay()));
            if (ate != null) filtros.add(cb.lessThan(root.get("dataHora"), ate.plusDays(1).atStartOfDay()));
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        return consultaRepository.findAll(spec, pageable).map(ConsultaResponse::de);
    }

    /** Agenda do dia de um médico, ordenada por horário. */
    @Transactional(readOnly = true)
    public List<ConsultaResponse> agenda(Long medicoId, LocalDate data) {
        if (!medicoRepository.existsById(medicoId)) {
            throw new RecursoNaoEncontradoException("Médico não encontrado: " + medicoId);
        }
        return consultaRepository
                .findByMedicoIdAndDataHoraBetweenOrderByDataHora(
                        medicoId, data.atStartOfDay(), data.plusDays(1).atStartOfDay().minusNanos(1))
                .stream()
                .map(ConsultaResponse::de)
                .toList();
    }

    @Transactional
    public ConsultaResponse confirmar(Long id) {
        return transicionar(id, CONFIRMADA, Set.of(AGENDADA));
    }

    @Transactional
    public ConsultaResponse iniciarAtendimento(Long id) {
        return transicionar(id, EM_ATENDIMENTO, Set.of(CONFIRMADA));
    }

    @Transactional
    public ConsultaResponse concluir(Long id) {
        return transicionar(id, CONCLUIDA, Set.of(EM_ATENDIMENTO));
    }

    @Transactional
    public ConsultaResponse registrarFalta(Long id) {
        return transicionar(id, FALTOU, Set.of(AGENDADA, CONFIRMADA));
    }

    @Transactional
    public ConsultaResponse cancelar(Long id, String motivo) {
        Consulta c = buscarEntidade(id);
        validarTransicao(c, CANCELADA, Set.of(AGENDADA, CONFIRMADA));
        c.setStatus(CANCELADA);
        c.setMotivoCancelamento(motivo);
        return ConsultaResponse.de(c);
    }

    private ConsultaResponse transicionar(Long id, StatusConsulta destino, Set<StatusConsulta> permitidos) {
        Consulta c = buscarEntidade(id);
        validarTransicao(c, destino, permitidos);
        c.setStatus(destino);
        return ConsultaResponse.de(c);
    }

    private void validarTransicao(Consulta c, StatusConsulta destino, Set<StatusConsulta> permitidos) {
        if (!permitidos.contains(c.getStatus())) {
            throw new RegraNegocioException(
                    "Não é possível alterar a consulta de %s para %s".formatted(c.getStatus(), destino));
        }
    }

    private Consulta buscarEntidade(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada: " + id));
    }
}
