package com.clinica.api.repository;

import com.clinica.api.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByCpf(String cpf);

    Page<Paciente> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
