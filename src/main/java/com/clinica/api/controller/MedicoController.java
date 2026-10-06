package com.clinica.api.controller;

import com.clinica.api.dto.MedicoRequest;
import com.clinica.api.dto.MedicoResponse;
import com.clinica.api.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicoResponse criar(@RequestBody @Valid MedicoRequest request) {
        return medicoService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public MedicoResponse atualizar(@PathVariable Long id, @RequestBody @Valid MedicoRequest request) {
        return medicoService.atualizar(id, request);
    }

    @GetMapping
    public Page<MedicoResponse> listar(@RequestParam(required = false) String especialidade,
                                       @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return medicoService.listar(especialidade, pageable);
    }

    @GetMapping("/{id}")
    public MedicoResponse buscar(@PathVariable Long id) {
        return medicoService.buscar(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        medicoService.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMIN')")
    public MedicoResponse reativar(@PathVariable Long id) {
        return medicoService.reativar(id);
    }
}
