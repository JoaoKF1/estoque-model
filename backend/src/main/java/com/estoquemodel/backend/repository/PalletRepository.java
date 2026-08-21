package com.estoquemodel.backend.repository;

import com.estoquemodel.backend.model.Pallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PalletRepository extends JpaRepository<Pallet, Long> {

    List<Pallet> findByUsuarioIdOrderByIdDesc(Long usuarioId);

    List<Pallet> findByUsuarioIdAndTipoContainingIgnoreCaseOrderByIdDesc(Long usuarioId, String tipo);

    /** Garante que um usuário só edite/apague os próprios pallets. */
    Optional<Pallet> findByIdAndUsuarioId(Long id, Long usuarioId);
}
