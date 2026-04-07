package com.devsenior.jmorera.bibliokeep.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record RegisterRequest(
		@Email
		@NotBlank
		String email,
		@NotBlank
		@Size(min = 8, max = 72)
		String password,
		Set<String> preferences,
		Integer annualGoal
) {
}

