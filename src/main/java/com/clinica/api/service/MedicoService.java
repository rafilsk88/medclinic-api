package com.clinica.api.service;

import com.clinica.api.dto.MedicoRequest;
import com.clinica.api.dto.MedicoResponse;
import com.clinica.api.exception.ConflitoException;
import com.clinica.api.exception.RecursoNaoEncontradoException;
import com.clinica.api.model.Medico;
import com.clinica.api.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicoService {

    private final MedicoRepository medicoRepository;

    @Transactional
    public MedicoResponse criar(MedicoRequest r) {
        if (medicoRepository.existsByCrm(r.crm())) {
            throw new ConflitoException("Já existe médico cadastrado com este CRM");
        }
        Medico m = Medico.builder()
                .nome(r.nome())
                .crm(r.crm())
                .especialidade(r.especialidade())
                .email(r.email())
                .telefone(r.telefone())
                .build();
        return MedicoResponse.de(medicoRepository.save(m));
    }

    @Transactional
    public MedicoResponse atualizar(Long id, MedicoRequest r) {
        Medico m = buscarEntidade(id);
        if (!m.getCrm().equals(r.crm()) && medicoRepository.existsByCrm(r.crm())) {
            throw new ConflitoException("Já existe médico cadastrado com este CRM");
        }
        m.setNome(r.nome());
        m.setCrm(r.crm());
        m.setEspecialidade(r.especialidade());
        m.setEmail(r.email());
        m.setTelefone(r.telefone());
        return MedicoResponse.de(m);
    }

    @Transactional(readOnly = true)
    public Page<MedicoResponse> listar(String especialidade, Pageable pageable) {
        Page<Medico> pagina = (especialidade == null || especialidade.isBlank())
                ? medicoRepository.findAll(pageable)
                : medicoRepository.findByEspecialidadeContainingIgnoreCase(especialidade, pageable);
        return pagina.map(MedicoResponse::de);
    }

    @Transactional(readOnly = true)
    public MedicoResponse buscar(Long id) {
        return MedicoResponse.de(buscarEntidade(id));
    }

    /** Exclusão lógica: preserva o histórico de consultas. */
    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).setAtivo(false);
    }

    @Transactional
    public MedicoResponse reativar(Long id) {
        Medico m = buscarEntidade(id);
        m.setAtivo(true);
        return MedicoResponse.de(m);
    }

    private Medico buscarEntidade(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico não encontrado: " + id));
    }
}
