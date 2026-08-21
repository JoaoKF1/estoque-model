package com.estoquemodel.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Código de confirmação de e-mail (RF02), enviado logo após o cadastro
 * (RF01). Sem confirmar, o usuário não consegue fazer login (ver
 * AuthService.login).
 */
@Entity
@Table(name = "email_confirmation_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código numérico de 6 dígitos, mais fácil de digitar do que um UUID. */
    @Column(nullable = false, length = 6)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado", nullable = false)
    private boolean usado;

    public boolean isExpirado() {
        return LocalDateTime.now().isAfter(this.expiraEm);
    }
}
