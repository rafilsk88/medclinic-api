package com.clinica.api.dto;

public record TokenResponse(String token, String tipo, long expiraEmSegundos, String perfil) {
}
