package com.estoquemodel.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PalletRequest {

    @NotBlank(message = "Informe o tipo do pallet")
    private String tipo;

    @NotNull(message = "Informe a frente (mm)")
    @DecimalMin(value = "0.01", message = "A frente deve ser maior que zero")
    private BigDecimal frenteMm;

    @NotNull(message = "Informe a profundidade (mm)")
    @DecimalMin(value = "0.01", message = "A profundidade deve ser maior que zero")
    private BigDecimal profundidadeMm;

    @NotNull(message = "Informe a altura (mm)")
    @DecimalMin(value = "0.01", message = "A altura deve ser maior que zero")
    private BigDecimal alturaMm;

    @NotNull(message = "Informe o peso (kg)")
    @DecimalMin(value = "0.01", message = "O peso deve ser maior que zero")
    private BigDecimal pesoKg;

    /** Opcional - ex.: material (madeira/plástico), estado de conservação. */
    @Size(max = 120, message = "A característica deve ter no máximo 120 caracteres")
    private String caracteristica;
}
