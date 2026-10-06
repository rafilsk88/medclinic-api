package com.clinica.api.service;

import com.clinica.api.dto.LoginRequest;
import com.clinica.api.dto.RegistrarUsuarioRequest;
import com.clinica.api.dto.TokenResponse;
import com.clinica.api.dto.UsuarioResponse;
import com.clinica.api.exception.ConflitoException;
import com.clinica.api.model.Usuario;
import com.clinica.api.repository.UsuarioRepository;
import com.clinica.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public TokenResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha()));
        UserDetails usuario = (UserDetails) auth.getPrincipal();
        String perfil = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.replace("ROLE_", ""))
                .findFirst().orElse("");
        return new TokenResponse(jwtService.gerarToken(usuario), "Bearer",
                jwtService.getExpiracaoSegundos(), perfil);
    }

    @Transactional
    public UsuarioResponse registrar(RegistrarUsuarioRequest r) {
        if (usuarioRepository.existsByEmail(r.email())) {
            throw new ConflitoException("Já existe um usuário com este e-mail");
        }
        Usuario u = Usuario.builder()
                .nome(r.nome())
                .email(r.email())
                .senha(passwordEncoder.encode(r.senha()))
                .perfil(r.perfil())
                .build();
        return UsuarioResponse.de(usuarioRepository.save(u));
    }
}
