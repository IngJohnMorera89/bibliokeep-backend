package com.devsenior.jmorera.bibliokeep.service.auth;

import com.devsenior.jmorera.bibliokeep.model.dto.auth.AuthTokensResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.LoginRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RefreshRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RegisterRequest;

public interface AuthService {

	void register(RegisterRequest request);

	AuthTokensResponse login(LoginRequest request);

	AuthTokensResponse refresh(RefreshRequest request);
}

