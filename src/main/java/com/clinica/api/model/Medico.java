package com.clinica.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String crm;

    @Column(nullable = false)
    private String especialidade;

    private String email;

    private String telefone;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;
}
