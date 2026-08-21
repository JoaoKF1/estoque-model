package com.estoquemodel.backend.service;

import com.estoquemodel.backend.dto.PalletRequest;
import com.estoquemodel.backend.dto.PalletResponse;
import com.estoquemodel.backend.exception.ApiException;
import com.estoquemodel.backend.model.Pallet;
import com.estoquemodel.backend.model.Usuario;
import com.estoquemodel.backend.repository.PalletRepository;
import com.estoquemodel.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * RF04 - Gerenciar Pallet. Cada usuário só enxerga e altera os próprios
 * pallets: toda consulta é filtrada por usuarioId, nunca por id "solto".
 */
@Service
@RequiredArgsConstructor
public class PalletService {

    private final PalletRepository palletRepository;
    private final UsuarioRepository usuarioRepository;

    public List<PalletResponse> listar(String emailUsuario, String tipo) {
        Usuario usuario = buscarUsuario(emailUsuario);

        List<Pallet> pallets = (tipo == null || tipo.isBlank())
                ? palletRepository.findByUsuarioIdOrderByIdDesc(usuario.getId())
                : palletRepository.findByUsuarioIdAndTipoContainingIgnoreCaseOrderByIdDesc(usuario.getId(), tipo.trim());

        return pallets.stream().map(this::paraResponse).toList();
    }

    @Transactional
    public PalletResponse criar(String emailUsuario, PalletRequest request) {
        Usuario usuario = buscarUsuario(emailUsuario);

        Pallet pallet = Pallet.builder()
                .tipo(request.getTipo().trim())
                .frenteMm(request.getFrenteMm())
                .profundidadeMm(request.getProfundidadeMm())
                .alturaMm(request.getAlturaMm())
                .pesoKg(request.getPesoKg())
                .caracteristica(normalizarCaracteristica(request.getCaracteristica()))
                .usuario(usuario)
                .build();

        palletRepository.save(pallet);
        return paraResponse(pallet);
    }

    @Transactional
    public PalletResponse atualizar(String emailUsuario, Long id, PalletRequest request) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Pallet pallet = buscarPalletDoUsuario(id, usuario.getId());

        pallet.setTipo(request.getTipo().trim());
        pallet.setFrenteMm(request.getFrenteMm());
        pallet.setProfundidadeMm(request.getProfundidadeMm());
        pallet.setAlturaMm(request.getAlturaMm());
        pallet.setPesoKg(request.getPesoKg());
        pallet.setCaracteristica(normalizarCaracteristica(request.getCaracteristica()));

        palletRepository.save(pallet);
        return paraResponse(pallet);
    }

    @Transactional
    public void deletar(String emailUsuario, Long id) {
        Usuario usuario = buscarUsuario(emailUsuario);
        Pallet pallet = buscarPalletDoUsuario(id, usuario.getId());
        palletRepository.delete(pallet);
    }

    private String normalizarCaracteristica(String caracteristica) {
        return (caracteristica == null || caracteristica.isBlank()) ? null : caracteristica.trim();
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Usuário não encontrado", HttpStatus.NOT_FOUND));
    }

    private Pallet buscarPalletDoUsuario(Long id, Long usuarioId) {
        return palletRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ApiException("Pallet não encontrado", HttpStatus.NOT_FOUND));
    }

    private PalletResponse paraResponse(Pallet pallet) {
        return PalletResponse.builder()
                .id(pallet.getId())
                .tipo(pallet.getTipo())
                .frenteMm(pallet.getFrenteMm())
                .profundidadeMm(pallet.getProfundidadeMm())
                .alturaMm(pallet.getAlturaMm())
                .pesoKg(pallet.getPesoKg())
                .caracteristica(pallet.getCaracteristica())
                .criadoEm(pallet.getCriadoEm())
                .build();
    }
}
