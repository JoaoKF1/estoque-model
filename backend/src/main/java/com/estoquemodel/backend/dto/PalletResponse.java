package com.estoquemodel.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PalletResponse {
    private Long id;
    private String tipo;
    private BigDecimal frenteMm;
    private BigDecimal profundidadeMm;
    private BigDecimal alturaMm;
    private BigDecimal pesoKg;
    private String caracteristica;
    private LocalDateTime criadoEm;
}
