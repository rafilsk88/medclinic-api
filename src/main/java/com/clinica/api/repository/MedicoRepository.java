package com.clinica.api.repository;

import com.clinica.api.model.Medico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByCrm(String crm);

    Page<Medico> findByEspecialidadeContainingIgnoreCase(String especialidade, Pageable pageable);
}
