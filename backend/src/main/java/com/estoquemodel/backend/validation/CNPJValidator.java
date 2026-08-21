package com.estoquemodel.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementa o algoritmo padrão de validação de CNPJ (módulo 11 sobre os
 * dois dígitos verificadores). Aceita o valor formatado (com pontos, barra
 * e hífen) ou só números - qualquer caractere que não seja dígito é
 * ignorado antes de validar.
 */
public class CNPJValidator implements ConstraintValidator<CNPJ, String> {

    private static final int[] PESOS_PRIMEIRO_DIGITO = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_SEGUNDO_DIGITO = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            // Presença/obrigatoriedade é responsabilidade de @NotBlank, não desta anotação.
            return true;
        }

        String digitos = valor.replaceAll("\\D", "");

        if (digitos.length() != 14) {
            return false;
        }

        // CNPJs com todos os dígitos iguais (ex.: 00000000000000) passam no
        // cálculo do módulo 11, mas não são CNPJs válidos de verdade.
        if (digitos.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigitoVerificador = calcularDigitoVerificador(digitos.substring(0, 12), PESOS_PRIMEIRO_DIGITO);
        int segundoDigitoVerificador = calcularDigitoVerificador(digitos.substring(0, 12) + primeiroDigitoVerificador, PESOS_SEGUNDO_DIGITO);

        String digitosVerificadoresCalculados = "" + primeiroDigitoVerificador + segundoDigitoVerificador;

        return digitos.endsWith(digitosVerificadoresCalculados);
    }

    private int calcularDigitoVerificador(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
