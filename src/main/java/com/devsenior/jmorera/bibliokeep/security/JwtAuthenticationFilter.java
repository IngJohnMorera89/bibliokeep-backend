package com.devsenior.jmorera.bibliokeep.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devsenior.jmorera.bibliokeep.service.auth.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		var authHeader = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
		if (authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		var token = authHeader.get().substring(7);
		var username = jwtService.extractUsernameFromAccessToken(token);

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			var userDetails = userDetailsService.loadUserByUsername(username);
			if (jwtService.isAccessTokenValid(token, userDetails)) {
				var authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		filterChain.doFilter(request, response);
	}
}
