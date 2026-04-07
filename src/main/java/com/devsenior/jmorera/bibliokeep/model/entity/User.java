package com.devsenior.jmorera.bibliokeep.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
		name = "users",
		indexes = {
				@Index(name = "idx_users_email", columnList = "email", unique = true)
		}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, unique = true, length = 320)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@ElementCollection
	@CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
	@Column(name = "preference", nullable = false, length = 80)
	@Builder.Default
	private Set<String> preferences = new HashSet<>();

	@Builder.Default
	@Column(nullable = false)
	private Integer annualGoal = 12;
}

