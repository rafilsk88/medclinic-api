package com.clinica.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank String nome,
        @NotBlank @CPF String cpf,
        @Email String email,
        String telefone,
        @NotNull @Past LocalDate dataNascimento) {
}
