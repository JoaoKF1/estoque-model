package com.estoquemodel.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa um usuário do sistema (RF01 - Cadastrar Usuário / RF03 - Fazer
 * login). Mapeia para a tabela "usuarios" no PostgreSQL.
 */
@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /**
     * Sempre armazenar o hash da senha (BCrypt), nunca a senha em texto puro.
     */
    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    /** Só dígitos (14), sem pontuação - a formatação é responsabilidade do front. */
    @Column(nullable = false, length = 14)
    private String cnpj;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(length = 120)
    private String empresa;

    @Column(length = 80)
    private String cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;

    /** RF02: sem confirmar o e-mail, o login é bloqueado (ver AuthService.login). */
    @Column(name = "email_confirmado", nullable = false)
    private boolean emailConfirmado;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
