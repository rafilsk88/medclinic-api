package com.clinica.api.service;

import com.clinica.api.dto.PacienteRequest;
import com.clinica.api.dto.PacienteResponse;
import com.clinica.api.exception.ConflitoException;
import com.clinica.api.exception.RecursoNaoEncontradoException;
import com.clinica.api.model.Paciente;
import com.clinica.api.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    @Transactional
    public PacienteResponse criar(PacienteRequest r) {
        if (pacienteRepository.existsByCpf(r.cpf())) {
            throw new ConflitoException("Já existe paciente cadastrado com este CPF");
        }
        Paciente p = Paciente.builder()
                .nome(r.nome())
                .cpf(r.cpf())
                .email(r.email())
                .telefone(r.telefone())
                .dataNascimento(r.dataNascimento())
                .build();
        return PacienteResponse.de(pacienteRepository.save(p));
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest r) {
        Paciente p = buscarEntidade(id);
        if (!p.getCpf().equals(r.cpf()) && pacienteRepository.existsByCpf(r.cpf())) {
            throw new ConflitoException("Já existe paciente cadastrado com este CPF");
        }
        p.setNome(r.nome());
        p.setCpf(r.cpf());
        p.setEmail(r.email());
        p.setTelefone(r.telefone());
        p.setDataNascimento(r.dataNascimento());
        return PacienteResponse.de(p);
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponse> listar(String nome, Pageable pageable) {
        Page<Paciente> pagina = (nome == null || nome.isBlank())
                ? pacienteRepository.findAll(pageable)
                : pacienteRepository.findByNomeContainingIgnoreCase(nome, pageable);
        return pagina.map(PacienteResponse::de);
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscar(Long id) {
        return PacienteResponse.de(buscarEntidade(id));
    }

    private Paciente buscarEntidade(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado: " + id));
    }
}
