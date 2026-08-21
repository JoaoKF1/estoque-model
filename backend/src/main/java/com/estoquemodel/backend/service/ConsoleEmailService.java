package com.estoquemodel.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementação provisória: em vez de enviar um e-mail de verdade,
 * apenas imprime o link no console/log do backend.
 *
 * Isso permite testar TODO o fluxo de "esqueci minha senha" hoje,
 * sem precisar configurar um servidor SMTP. Quando o grupo quiser
 * enviar e-mails reais, basta implementar EmailService com
 * JavaMailSender (dependência spring-boot-starter-mail) e marcar
 * essa classe com @Primary trocado para a nova implementação.
 */
@Service
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
