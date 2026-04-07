package com.devsenior.jmorera.bibliokeep.controller;

import com.devsenior.jmorera.bibliokeep.model.dto.auth.AuthTokensResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.LoginRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RefreshRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RegisterRequest;
import com.devsenior.jmorera.bibliokeep.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
		authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/login")
	public ResponseEntity<AuthTokensResponse> login(@Valid @RequestBody LoginRequest request) {
		var tokens = authService.login(request);
		return ResponseEntity.ok(tokens);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthTokensResponse> refresh(@Valid @RequestBody RefreshRequest request) {
		var tokens = authService.refresh(request);
		return ResponseEntity.ok(tokens);
	}
}

