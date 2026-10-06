package com.clinica.api.model;

/**
 * Fluxo de atendimento:
 * AGENDADA -> CONFIRMADA -> EM_ATENDIMENTO -> CONCLUIDA
 * AGENDADA/CONFIRMADA -> CANCELADA | FALTOU
 */
public enum StatusConsulta {
    AGENDADA,
    CONFIRMADA,
    EM_ATENDIMENTO,
    CONCLUIDA,
    CANCELADA,
    FALTOU
}
