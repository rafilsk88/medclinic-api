package com.clinica.api.exception;

import java.time.Instant;
import java.util.List;

public record ErroResposta(Instant timestamp, int status, String erro, String mensagem, List<String> detalhes) {

    public static ErroResposta de(int status, String erro, String mensagem) {
        return new ErroResposta(Instant.now(), status, erro, mensagem, List.of());
    }
}
