package com.devsenior.jmorera.bibliokeep.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final String accessSecret;
	private final int accessExpirationMinutes;
	private final String refreshSecret;
	private final int refreshExpirationDays;

	public JwtService(
			@Value("${jwt.access-token.secret}") String accessSecret,
			@Value("${jwt.access-token.expiration-minutes}") int accessExpirationMinutes,
			@Value("${jwt.refresh-token.secret}") String refreshSecret,
			@Value("${jwt.refresh-token.expiration-days}") int refreshExpirationDays
	) {
		this.accessSecret = accessSecret;
		this.accessExpirationMinutes = accessExpirationMinutes;
		this.refreshSecret = refreshSecret;
		this.refreshExpirationDays = refreshExpirationDays;
	}

	public String generateAccessToken(UserDetails user, Map<String, Object> extraClaims) {
		var now = Instant.now();
		var expiry = now.plus(accessExpirationMinutes, ChronoUnit.MINUTES);
		return Jwts.builder()
				.subject(user.getUsername())
				.claims(extraClaims)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiry))
				.signWith(getAccessKey())
				.compact();
	}

	public String generateRefreshToken(UserDetails user) {
		return generateRefreshToken(user, UUID.randomUUID().toString());
	}

	public String generateRefreshToken(UserDetails user, String refreshTokenId) {
		var now = Instant.now();
		var expiry = now.plus(refreshExpirationDays, ChronoUnit.DAYS);
		return Jwts.builder()
				.id(refreshTokenId)
				.subject(user.getUsername())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiry))
				.signWith(getRefreshKey())
				.compact();
	}

	public String extractUsernameFromAccessToken(String token) {
		return extractAllClaims(token, true).getSubject();
	}

	public String extractUsernameFromRefreshToken(String token) {
		return extractAllClaims(token, false).getSubject();
	}

	public String extractRefreshTokenId(String token) {
		return extractAllClaims(token, false).getId();
	}

	public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
		var username = extractUsernameFromRefreshToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token, false);
	}

	public boolean isAccessTokenValid(String token, UserDetails userDetails) {
		var username = extractUsernameFromAccessToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token, true);
	}

	private boolean isTokenExpired(String token, boolean access) {
		return extractAllClaims(token, access).getExpiration().before(new Date());
	}

	private Claims extractAllClaims(String token, boolean access) {
		return Jwts.parser()
				.verifyWith(access ? getAccessKey() : getRefreshKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey getAccessKey() {
		var keyBytes = Decoders.BASE64.decode(accessSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private SecretKey getRefreshKey() {
		var keyBytes = Decoders.BASE64.decode(refreshSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}

