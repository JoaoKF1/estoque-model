package com.estoquemodel.backend.repository;

import com.estoquemodel.backend.model.EmailConfirmationToken;
import com.estoquemodel.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findTopByUsuarioAndCodigoOrderByIdDesc(Usuario usuario, String codigo);

    Optional<EmailConfirmationToken> findTopByUsuarioOrderByIdDesc(Usuario usuario);
}
