package com.clinica.api.dto;

import com.clinica.api.model.Medico;

public record MedicoResponse(Long id, String nome, String crm, String especialidade,
                             String email, String telefone, boolean ativo) {

    public static MedicoResponse de(Medico m) {
        return new MedicoResponse(m.getId(), m.getNome(), m.getCrm(), m.getEspecialidade(),
                m.getEmail(), m.getTelefone(), m.isAtivo());
    }
}
