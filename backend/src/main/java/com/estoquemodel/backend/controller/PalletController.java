package com.estoquemodel.backend.controller;

import com.estoquemodel.backend.dto.PalletRequest;
import com.estoquemodel.backend.dto.PalletResponse;
import com.estoquemodel.backend.service.PalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RF04 - Gerenciar Pallet. Todos os endpoints exigem JWT válido (ver
 * SecurityConfig/JwtAuthFilter) e operam sempre sobre os pallets do
 * usuário logado (Authentication#getName() = e-mail, é o "username" do
 * Spring Security - ver UsuarioDetailsService).
 */
@RestController
@RequestMapping("/api/pallets")
@RequiredArgsConstructor
public class PalletController {

    private final PalletService palletService;

    @GetMapping
    public ResponseEntity<List<PalletResponse>> listar(
            Authentication authentication,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(palletService.listar(authentication.getName(), tipo));
    }

    @PostMapping
    public ResponseEntity<PalletResponse> criar(Authentication authentication,
                                                 @Valid @RequestBody PalletRequest request) {
        return ResponseEntity.ok(palletService.criar(authentication.getName(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PalletResponse> atualizar(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody PalletRequest request) {
        return ResponseEntity.ok(palletService.atualizar(authentication.getName(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(Authentication authentication, @PathVariable Long id) {
        palletService.deletar(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
