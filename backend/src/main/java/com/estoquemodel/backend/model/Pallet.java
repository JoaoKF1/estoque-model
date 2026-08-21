package com.estoquemodel.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RF04 - Gerenciar Pallet. Cada pallet pertence a um usuário (dono do
 * cadastro) e será usado depois na simulação (RF07) para calcular a
 * estrutura de armazenagem recomendada.
 */
@Entity
@Table(name = "pallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(name = "frente_mm", nullable = false)
    private BigDecimal frenteMm;

    @Column(name = "profundidade_mm", nullable = false)
    private BigDecimal profundidadeMm;

    @Column(name = "altura_mm", nullable = false)
    private BigDecimal alturaMm;

    @Column(name = "peso_kg", nullable = false)
    private BigDecimal pesoKg;

    /** Campo livre do diagrama de classes do TG (ex.: material, estado de conservação). Opcional. */
    @Column(length = 120)
    private String caracteristica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
