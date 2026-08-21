package com.estoquemodel.backend.controller;

import com.estoquemodel.backend.dto.*;
import com.estoquemodel.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<MessageResponse> confirmEmail(@Valid @RequestBody ConfirmEmailRequest request) {
        return ResponseEntity.ok(authService.confirmarEmail(request));
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<MessageResponse> resendConfirmation(@Valid @RequestBody ReenviarConfirmacaoRequest request) {
        return ResponseEntity.ok(authService.reenviarConfirmacao(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.esqueciSenha(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.redefinirSenha(request));
    }
}
