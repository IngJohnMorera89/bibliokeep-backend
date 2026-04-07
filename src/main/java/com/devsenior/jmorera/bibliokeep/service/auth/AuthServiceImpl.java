package com.devsenior.jmorera.bibliokeep.service.auth;

import com.devsenior.jmorera.bibliokeep.exception.InvalidRefreshTokenException;
import com.devsenior.jmorera.bibliokeep.exception.RefreshTokenExpiredException;
import com.devsenior.jmorera.bibliokeep.mapper.UserMapper;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.AuthTokensResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.LoginRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RefreshRequest;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.RegisterRequest;
import com.devsenior.jmorera.bibliokeep.model.entity.RefreshToken;
import com.devsenior.jmorera.bibliokeep.repository.RefreshTokenRepository;
import com.devsenior.jmorera.bibliokeep.repository.UserRepository;
import com.devsenior.jmorera.bibliokeep.security.JwtService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Override
	@Transactional
	public void register(RegisterRequest request) {
		var user = userMapper.toEntity(request);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	@Override
	@Transactional
	public AuthTokensResponse login(LoginRequest request) {
		var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
		authenticationManager.authenticate(authToken);

		var user = userRepository.findByEmail(request.email())
				.orElseThrow();

		var userDetails = buildUserDetails(user.getEmail(), user.getPassword());
		var accessToken = jwtService.generateAccessToken(userDetails, Map.of());

		refreshTokenRepository.deleteByUserId(user.getId());
		var refreshTokenId = UUID.randomUUID().toString();
		var refreshToken = RefreshToken.builder()
				.id(refreshTokenId)
				.userId(user.getId())
				.expiresAt(Instant.now().plus(30, ChronoUnit.DAYS))
				.build();
		refreshTokenRepository.save(refreshToken);

		var refreshJwt = jwtService.generateRefreshToken(userDetails, refreshTokenId);
		return new AuthTokensResponse(accessToken, refreshJwt);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthTokensResponse refresh(RefreshRequest request) {
		var token = request.refreshToken();
		var refreshTokenId = jwtService.extractRefreshTokenId(token);
		var storedToken = refreshTokenRepository.findByIdAndRevokedFalse(refreshTokenId)
				.orElseThrow(() -> new InvalidRefreshTokenException("Refresh token inválido o revocado"));

		if (storedToken.getExpiresAt().isBefore(Instant.now())) {
			throw new RefreshTokenExpiredException("Refresh token expirado");
		}

		var username = jwtService.extractUsernameFromRefreshToken(token);
		var user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		var userDetails = buildUserDetails(user.getEmail(), user.getPassword());
		if (!jwtService.isRefreshTokenValid(token, userDetails)) {
			throw new InvalidRefreshTokenException("Refresh token inválido");
		}

		var newAccessToken = jwtService.generateAccessToken(userDetails, Map.of());
		return new AuthTokensResponse(newAccessToken, token);
	}

	private UserDetails buildUserDetails(String email, String password) {
		var authorities = java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"));
		return new User(email, password, authorities);
	}
}

