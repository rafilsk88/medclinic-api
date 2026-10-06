package com.clinica.api.dto;

import com.clinica.api.model.Consulta;
import com.clinica.api.model.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponse(Long id, Long pacienteId, String pacienteNome,
                               Long medicoId, String medicoNome, String especialidade,
                               LocalDateTime dataHora, StatusConsulta status,
                               String observacoes, String motivoCancelamento) {

    public static ConsultaResponse de(Consulta c) {
        return new ConsultaResponse(
                c.getId(),
                c.getPaciente().getId(), c.getPaciente().getNome(),
                c.getMedico().getId(), c.getMedico().getNome(), c.getMedico().getEspecialidade(),
                c.getDataHora(), c.getStatus(), c.getObservacoes(), c.getMotivoCancelamento());
    }
}
