package com.devsenior.jmorera.bibliokeep.repository;

import com.devsenior.jmorera.bibliokeep.model.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

	Optional<RefreshToken> findByIdAndRevokedFalse(String id);

	void deleteByUserId(UUID userId);
}

