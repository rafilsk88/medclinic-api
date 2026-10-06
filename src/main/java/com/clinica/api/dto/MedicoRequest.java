package com.clinica.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MedicoRequest(
        @NotBlank String nome,
        @NotBlank @Pattern(regexp = "^[0-9]{4,10}-[A-Z]{2}$", message = "CRM deve seguir o formato 123456-SP") String crm,
        @NotBlank String especialidade,
        @Email String email,
        String telefone) {
}
