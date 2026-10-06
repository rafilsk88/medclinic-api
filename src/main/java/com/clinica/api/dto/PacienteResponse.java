package com.clinica.api.dto;

import com.clinica.api.model.Paciente;

import java.time.LocalDate;

public record PacienteResponse(Long id, String nome, String cpf, String email,
                               String telefone, LocalDate dataNascimento) {

    public static PacienteResponse de(Paciente p) {
        return new PacienteResponse(p.getId(), p.getNome(), p.getCpf(), p.getEmail(),
                p.getTelefone(), p.getDataNascimento());
    }
}
