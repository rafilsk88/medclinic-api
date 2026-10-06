package com.clinica.api.config;

import com.clinica.api.model.Perfil;
import com.clinica.api.model.Usuario;
import com.clinica.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner criarAdminPadrao(UsuarioRepository repo,
                                       PasswordEncoder encoder,
                                       @Value("${app.admin.email}") String email,
                                       @Value("${app.admin.senha}") String senha) {
        return args -> {
            if (!repo.existsByEmail(email)) {
                repo.save(Usuario.builder()
                        .nome("Administrador")
                        .email(email)
                        .senha(encoder.encode(senha))
                        .perfil(Perfil.ADMIN)
                        .build());
                log.info("Usuário administrador padrão criado: {}", email);
            }
        };
    }
}
