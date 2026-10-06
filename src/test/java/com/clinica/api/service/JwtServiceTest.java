package com.clinica.api.service;

import com.clinica.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-mais-de-32-caracteres!!";

    private UserDetails usuario() {
        return User.builder().username("ana@clinica.com").password("x").roles("MEDICO").build();
    }

    @Test
    void deveGerarEValidarToken() {
        JwtService jwt = new JwtService(SEGREDO, 5);
        String token = jwt.gerarToken(usuario());

        assertEquals("ana@clinica.com", jwt.extrairUsername(token));
        assertTrue(jwt.tokenValido(token, usuario()));
    }

    @Test
    void deveRejeitarTokenExpirado() {
        JwtService jwt = new JwtService(SEGREDO, -1);
        String token = jwt.gerarToken(usuario());

        assertFalse(jwt.tokenValido(token, usuario()));
    }

    @Test
    void deveRejeitarSegredoCurto() {
        assertThrows(IllegalStateException.class, () -> new JwtService("curto", 5));
    }
}
