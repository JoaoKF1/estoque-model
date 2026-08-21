package com.estoquemodel.backend.dto;

import com.estoquemodel.backend.validation.CNPJ;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 120, message = "O nome deve ter entre 2 e 120 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Informe um e-mail válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "O CNPJ é obrigatório")
    @CNPJ(message = "Informe um CNPJ válido")
    private String cnpj;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "[\\d()\\-\\s+]{8,20}", message = "Informe um telefone válido")
    private String telefone;

    @Size(max = 120, message = "O nome da empresa deve ter no máximo 120 caracteres")
    private String empresa;

    @Size(max = 80, message = "O cargo deve ter no máximo 80 caracteres")
    private String cargo;
}
