package com.estoquemodel.backend.service;

/**
 * Abstração para envio de e-mail. A implementação padrão (ConsoleEmailService)
 * apenas registra no log - suficiente para desenvolvimento/apresentação do
 * projeto acadêmico. Quando quiserem enviar e-mail de verdade, criem uma
 * implementação com Spring Mail (JavaMailSender) usando SMTP do Gmail,
 * Mailtrap, Brevo, etc., e troquem o @Primary para ela.
 */
public interface EmailService {

    void enviarLinkRedefinicaoSenha(String destinatario, String nome, String token);

    /** RF02 - Confirmar e-mail: envia o código de confirmação após o cadastro (RF01). */
    void enviarCodigoConfirmacao(String destinatario, String nome, String codigo);
}
