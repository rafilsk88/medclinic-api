package com.clinica.api.controller;

import com.clinica.api.dto.LoginRequest;
import com.clinica.api.dto.RegistrarUsuarioRequest;
import com.clinica.api.dto.TokenResponse;
import com.clinica.api.dto.UsuarioResponse;
import com.clinica.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody @Valid RegistrarUsuarioRequest request) {
        return authService.registrar(request);
    }
}
