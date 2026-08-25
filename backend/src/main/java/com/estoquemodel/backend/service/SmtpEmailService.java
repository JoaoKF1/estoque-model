package com.estoquemodel.backend.service;

import com.estoquemodel.backend.exception.ApiException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Envio real de e-mail via SMTP (configurado para o Gmail em
 * application.properties).
 *
 * Só entra no lugar do {@link ConsoleEmailService} quando
 * app.mail.enabled=true, o que evita quebrar o ambiente de
 * desenvolvimento de quem não tem credenciais de SMTP configuradas.
 *
 * Os dois métodos são @Async: o envio acontece numa thread da pool
 * "emailExecutor" (ver AsyncConfig), então a resposta HTTP do cadastro /
 * "esqueci minha senha" volta na hora, sem esperar o Gmail. Como
 * consequência, uma falha de envio NÃO chega mais a quem chamou - ela é
 * registrada no log pelo AsyncUncaughtExceptionHandler.
 */
@Service
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${app.mail.from:}")
    private String remetente;

    @Value("${app.mail.from-name:Estoque Model}")
    private String nomeRemetente;

    public SmtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async("emailExecutor")
    public void enviarLinkRedefinicaoSenha(String destinatario, String nome, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;

        String html = """
                <p>Olá, %s!</p>
                <p>Recebemos um pedido para redefinir a senha da sua conta no
                <strong>Estoque Model</strong>. Clique no botão abaixo para escolher
                uma nova senha:</p>
                <p><a href="%s"
                      style="display:inline-block;padding:12px 20px;background:#1f6feb;
                             color:#ffffff;text-decoration:none;border-radius:6px">
                   Redefinir minha senha</a></p>
                <p>Se o botão não funcionar, copie e cole este endereço no navegador:<br>
                <a href="%s">%s</a></p>
                <p>O link é válido por tempo limitado. Se não foi você que fez esse
                pedido, ignore este e-mail - sua senha continua a mesma.</p>
                """.formatted(nome, link, link, link);

        enviar(destinatario, "Redefinição de senha - Estoque Model", html);
    }

    @Override
    @Async("emailExecutor")
    public void enviarCodigoConfirmacao(String destinatario, String nome, String codigo) {
        String html = """
                <p>Olá, %s!</p>
                <p>Seu cadastro no <strong>Estoque Model</strong> foi criado. Para
                ativar a conta, informe o código de confirmação abaixo:</p>
                <p style="font-size:28px;font-weight:bold;letter-spacing:4px">%s</p>
                <p>O código é válido por tempo limitado. Se não foi você que se
                cadastrou, basta ignorar este e-mail.</p>
                """.formatted(nome, codigo);

        enviar(destinatario, "Confirme seu e-mail - Estoque Model", html);
    }

    private void enviar(String destinatario, String assunto, String corpoHtml) {
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, false, StandardCharsets.UTF_8.name());

            if (remetente != null && !remetente.isBlank()) {
                helper.setFrom(remetente, nomeRemetente);
            }
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(mensagem);
            log.info("E-mail \"{}\" enviado para {}", assunto, destinatario);
        } catch (Exception ex) {
            // O motivo real (credencial errada, porta bloqueada, etc.) fica no log
            // do servidor; o usuário recebe apenas uma mensagem genérica.
            log.error("Falha ao enviar e-mail \"{}\" para {}", assunto, destinatario, ex);
            throw new ApiException(
                    "Não foi possível enviar o e-mail agora. Tente novamente em alguns instantes.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
