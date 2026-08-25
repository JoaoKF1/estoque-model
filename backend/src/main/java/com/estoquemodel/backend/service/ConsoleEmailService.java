package com.estoquemodel.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementação de desenvolvimento: em vez de enviar um e-mail de verdade,
 * apenas imprime o link/código no console/log do backend.
 *
 * Isso permite testar TODO o fluxo de "esqueci minha senha" e de confirmação
 * de cadastro sem credenciais de SMTP. É o comportamento padrão; para enviar
 * e-mails reais defina app.mail.enabled=true (aí entra o
 * {@link SmtpEmailService}).
 */
@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void enviarLinkRedefinicaoSenha(String destinatario, String nome, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;

        log.info("========================================================");
        log.info("[EMAIL SIMULADO] Redefinição de senha para: {} <{}>", nome, destinatario);
        log.info("Link (válido por tempo limitado): {}", link);
        log.info("========================================================");
    }

    @Override
    public void enviarCodigoConfirmacao(String destinatario, String nome, String codigo) {
        log.info("========================================================");
        log.info("[EMAIL SIMULADO] Confirmação de cadastro para: {} <{}>", nome, destinatario);
        log.info("Código de confirmação (válido por tempo limitado): {}", codigo);
        log.info("========================================================");
    }
}
