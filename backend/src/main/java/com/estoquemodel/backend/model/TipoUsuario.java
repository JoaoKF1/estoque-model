package com.estoquemodel.backend.model;

/**
 * Distingue usuário comum de administrador (campo "tipoUser" no diagrama
 * de classes do TG). Usado para restringir endpoints como Gerenciar
 * Estrutura e Gerenciar Usuário (RF06/RF11) só para ADMIN.
 *
 * O cadastro público (RF01) sempre cria um USUARIO comum - promover
 * alguém a ADMIN é feito diretamente no banco por enquanto, até a tela
 * de Gerenciar Usuário (Sprint 6) existir.
 */
public enum TipoUsuario {
    USUARIO,
    ADMIN
}
