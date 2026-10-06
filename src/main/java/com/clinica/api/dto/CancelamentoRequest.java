package com.clinica.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequest(@NotBlank String motivo) {
}
