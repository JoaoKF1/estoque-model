package com.estoquemodel.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção genérica para erros de regra de negócio, com o status HTTP
 * que deve ser retornado ao front-end.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
