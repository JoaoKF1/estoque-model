package com.estoquemodel.backend.service;

import com.estoquemodel.backend.dto.*;
import com.estoquemodel.backend.exception.ApiException;
import com.estoquemodel.backend.model.EmailConfirmationToken;
import com.estoquemodel.backend.model.PasswordResetToken;
import com.estoquemodel.backend.model.TipoUsuario;
import com.estoquemodel.backend.model.Usuario;
import com.estoquemodel.backend.repository.EmailConfirmationTokenRepository;
import com.estoquemodel.backend.repository.PasswordResetTokenRepository;
import com.estoquemodel.backend.repository.UsuarioRepository;
import com.estoquemodel.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final int RESET_TOKEN_VALIDADE_MINUTOS = 30;
    private static final int CONFIRMACAO_VALIDADE_MINUTOS = 30;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public MessageResponse registrar(RegisterRequest request) {
        String emailNormalizado = request.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new ApiException("Já existe uma conta cadastrada com esse e-mail", HttpStatus.CONFLICT);
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome().trim())
                .email(emailNormalizado)
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .cnpj(request.getCnpj().replaceAll("\\D", ""))
                .telefone(request.getTelefone().replaceAll("[^\\d]", ""))
                .empresa(request.getEmpresa() != null ? request.getEmpresa().trim() : null)
                .cargo(request.getCargo() != null ? request.getCargo().trim() : null)
                .tipoUsuario(TipoUsuario.USUARIO)
                .emailConfirmado(false)
                .build();

        usuarioRepository.save(usuario);

        // O envio é assíncrono (ver SmtpEmailService/AsyncConfig), então aqui só
        // sabemos se a tarefa foi aceita na fila - não se o Gmail aceitou a
        // mensagem. De qualquer forma uma falha não descarta um cadastro
        // válido: a conta fica criada e a pessoa pede um novo código em
        // /api/auth/resend-confirmation.
        boolean enviado = enviarNovoCodigoConfirmacao(usuario);

        if (!enviado) {
            return new MessageResponse(
                    "Cadastro realizado, mas não conseguimos enviar o código de confirmação agora. "
                            + "Use a opção de reenviar código em alguns instantes."
            );
        }

        return new MessageResponse(
                "Cadastro realizado com sucesso. Enviamos um código de confirmação para o seu e-mail."
        );
    }

    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Lança BadCredentialsException automaticamente se e-mail/senha não baterem.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("E-mail ou senha inválidos", HttpStatus.UNAUTHORIZED));

        if (!usuario.isEmailConfirmado()) {
            throw new ApiException(
                    "Confirme seu e-mail antes de fazer login. Verifique sua caixa de entrada ou peça um novo código.",
                    HttpStatus.FORBIDDEN
            );
        }

        String token = jwtUtil.gerarToken(usuario.getEmail());

        return new LoginResponse(token, usuario.getNome(), usuario.getEmail());
    }

    @Transactional
    public MessageResponse confirmarEmail(ConfirmEmailRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("E-mail ou código inválidos", HttpStatus.BAD_REQUEST));

        if (usuario.isEmailConfirmado()) {
            return new MessageResponse("Este e-mail já foi confirmado. Você já pode fazer login.");
        }

        EmailConfirmationToken confirmationToken = confirmationTokenRepository
                .findTopByUsuarioAndCodigoOrderByIdDesc(usuario, request.getCodigo().trim())
                .orElseThrow(() -> new ApiException("Código inválido", HttpStatus.BAD_REQUEST));

        if (confirmationToken.isUsado()) {
            throw new ApiException("Este código já foi utilizado", HttpStatus.BAD_REQUEST);
        }

        if (confirmationToken.isExpirado()) {
            throw new ApiException("Este código expirou. Solicite um novo.", HttpStatus.BAD_REQUEST);
        }

        usuario.setEmailConfirmado(true);
        usuarioRepository.save(usuario);

        confirmationToken.setUsado(true);
        confirmationTokenRepository.save(confirmationToken);

        return new MessageResponse("E-mail confirmado com sucesso. Você já pode fazer login.");
    }

    @Transactional
    public MessageResponse reenviarConfirmacao(ReenviarConfirmacaoRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Mesma resposta exista ou não o e-mail / já esteja confirmado, por segurança.
        usuarioRepository.findByEmail(email)
                .filter(usuario -> !usuario.isEmailConfirmado())
                .ifPresent(this::enviarNovoCodigoConfirmacao);

        return new MessageResponse(
                "Se o e-mail informado estiver cadastrado e ainda não confirmado, um novo código foi enviado."
        );
    }

    /**
     * @return true se o envio foi aceito; false se nem chegou a ser tentado
     *         (fila de e-mail cheia, ou falha imediata numa implementação
     *         síncrona). O motivo sempre vai para o log.
     */
    private boolean enviarNovoCodigoConfirmacao(Usuario usuario) {
        String codigo = gerarCodigoNumerico();

        EmailConfirmationToken token = EmailConfirmationToken.builder()
                .codigo(codigo)
                .usuario(usuario)
                .expiraEm(LocalDateTime.now().plusMinutes(CONFIRMACAO_VALIDADE_MINUTOS))
                .usado(false)
                .build();

        confirmationTokenRepository.save(token);

        try {
            emailService.enviarCodigoConfirmacao(usuario.getEmail(), usuario.getNome(), codigo);
            return true;
        } catch (RuntimeException ex) {
            log.error("Não foi possível enviar o código de confirmação para {}", usuario.getEmail(), ex);
            return false;
        }
    }

    private String gerarCodigoNumerico() {
        int numero = RANDOM.nextInt(1_000_000); // 0..999999
        return String.format("%06d", numero);
    }

    @Transactional
    public MessageResponse esqueciSenha(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Por segurança, a resposta é sempre a mesma, exista ou não o e-mail
        // (evita que alguém descubra quais e-mails estão cadastrados).
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .usuario(usuario)
                    .expiraEm(LocalDateTime.now().plusMinutes(RESET_TOKEN_VALIDADE_MINUTOS))
                    .usado(false)
                    .build();

            tokenRepository.save(resetToken);

            // A falha de envio NÃO pode virar erro HTTP: se um e-mail cadastrado
            // respondesse 503 e um não cadastrado respondesse 200, a resposta
            // deixaria de ser sempre igual e revelaria quais e-mails existem.
            // Com o envio assíncrono isso já não aconteceria, mas o try/catch
            // fica como garantia caso a implementação volte a ser síncrona.
            try {
                emailService.enviarLinkRedefinicaoSenha(usuario.getEmail(), usuario.getNome(), resetToken.getToken());
            } catch (RuntimeException ex) {
                log.error("Não foi possível enviar o link de redefinição para {}", usuario.getEmail(), ex);
            }
        });

        return new MessageResponse(
                "Se o e-mail informado estiver cadastrado, você receberá as instruções para redefinir sua senha."
        );
    }

    @Transactional
    public MessageResponse redefinirSenha(ResetPasswordRequest request) {
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ApiException("Token inválido", HttpStatus.BAD_REQUEST));

        if (resetToken.isUsado()) {
            throw new ApiException("Este token já foi utilizado", HttpStatus.BAD_REQUEST);
        }

        if (resetToken.isExpirado()) {
            throw new ApiException("Este token expirou. Solicite a redefinição novamente.", HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setSenhaHash(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        tokenRepository.save(resetToken);

        return new MessageResponse("Senha redefinida com sucesso. Você já pode fazer login.");
    }
}
