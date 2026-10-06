package com.clinica.api.controller;

import com.clinica.api.dto.PacienteRequest;
import com.clinica.api.dto.PacienteResponse;
import com.clinica.api.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponse criar(@RequestBody @Valid PacienteRequest request) {
        return pacienteService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public PacienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid PacienteRequest request) {
        return pacienteService.atualizar(id, request);
    }

    @GetMapping
    public Page<PacienteResponse> listar(@RequestParam(required = false) String nome,
                                         @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return pacienteService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscar(@PathVariable Long id) {
        return pacienteService.buscar(id);
    }
}
