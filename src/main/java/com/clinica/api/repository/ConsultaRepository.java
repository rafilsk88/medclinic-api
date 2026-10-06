package com.clinica.api.repository;

import com.clinica.api.model.Consulta;
import com.clinica.api.model.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long>, JpaSpecificationExecutor<Consulta> {

    /** Existe consulta ativa do médico dentro da janela (exclusiva) informada? */
    @Query("""
            select count(c) > 0 from Consulta c
            where c.medico.id = :medicoId
              and c.status not in :ignorados
              and c.dataHora > :inicio
              and c.dataHora < :fim
            """)
    boolean existeConflito(@Param("medicoId") Long medicoId,
                           @Param("inicio") LocalDateTime inicio,
                           @Param("fim") LocalDateTime fim,
                           @Param("ignorados") Collection<StatusConsulta> ignorados);

    List<Consulta> findByMedicoIdAndDataHoraBetweenOrderByDataHora(Long medicoId,
                                                                   LocalDateTime inicio,
                                                                   LocalDateTime fim);
}
