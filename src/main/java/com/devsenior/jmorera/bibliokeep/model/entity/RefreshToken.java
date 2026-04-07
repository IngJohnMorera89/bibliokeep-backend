package com.devsenior.jmorera.bibliokeep.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "refresh_tokens",
		indexes = {
				@Index(name = "idx_refresh_token_user", columnList = "user_id")
		}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

	@Id
	@Column(length = 100, nullable = false, updatable = false)
	private String id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	@Column(name = "revoked", nullable = false)
	@Builder.Default
	private boolean revoked = false;
}

