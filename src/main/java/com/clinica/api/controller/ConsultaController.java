package com.clinica.api.controller;

import com.clinica.api.dto.CancelamentoRequest;
import com.clinica.api.dto.ConsultaRequest;
import com.clinica.api.dto.ConsultaResponse;
import com.clinica.api.model.StatusConsulta;
import com.clinica.api.service.ConsultaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse agendar(@RequestBody @Valid ConsultaRequest request) {
        return consultaService.agendar(request);
    }

    @GetMapping
    public Page<ConsultaResponse> listar(
            @RequestParam(required = false) Long medicoId,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) StatusConsulta status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        return consultaService.listar(medicoId, pacienteId, status, de, ate, pageable);
    }

    @GetMapping("/agenda")
    public List<ConsultaResponse> agenda(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return consultaService.agenda(medicoId, data);
    }

    @GetMapping("/{id}")
    public ConsultaResponse buscar(@PathVariable Long id) {
        return consultaService.buscar(id);
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ConsultaResponse confirmar(@PathVariable Long id) {
        return consultaService.confirmar(id);
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ConsultaResponse iniciar(@PathVariable Long id) {
        return consultaService.iniciarAtendimento(id);
    }

    @PatchMapping("/{id}/concluir")
    @PreAuthorize("hasAnyRole('ADMIN','MEDICO')")
    public ConsultaResponse concluir(@PathVariable Long id) {
        return consultaService.concluir(id);
    }

    @PatchMapping("/{id}/faltou")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ConsultaResponse faltou(@PathVariable Long id) {
        return consultaService.registrarFalta(id);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPCIONISTA')")
    public ConsultaResponse cancelar(@PathVariable Long id, @RequestBody @Valid CancelamentoRequest request) {
        return consultaService.cancelar(id, request.motivo());
    }
}
