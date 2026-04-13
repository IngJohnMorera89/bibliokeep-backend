
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.devsenior.jmorera.bibliokeep.model.dto.auth.AuthTokensResponse;
import com.devsenior.jmorera.bibliokeep.model.dto.auth.LoginRequest;



/**
 * BACKEND ACTION REQUIRED
 * 
 * Location: src/main/java/com/devsenior/jmorera/bibliokeep/service/auth/AuthServiceImpl.java
 * Method: login()
 * 
 * Find this line (around line 57):
 * var accessToken = jwtService.generateAccessToken(userDetails, Map.of());
 * 
 * Replace with:
 */

@Override
@Transactional
public AuthTokensResponse login(LoginRequest request) {
    var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
    authenticationManager.authenticate(authToken);

    var user = userRepository.findByEmail(request.email())
            .orElseThrow();

    var userDetails = buildUserDetails(user.getEmail(), user.getPassword());
    
    // ✅ ADD THESE CUSTOM CLAIMS TO JWT
    var extraClaims = Map.of(
        "userId", user.getId().toString(),           // UUID → String
        "email", user.getEmail(),                    // User email
        "preferences", user.getPreferences(),         // Set<String> of genres
        "annualGoal", user.getAnnualGoal()           // Integer goal
    );
    
    var accessToken = jwtService.generateAccessToken(userDetails, extraClaims);

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

/**
 * RESULT: JWT accessToken will now contain:
 * 
 * {
 *   "sub": "user@example.com",
 *   "userId": "550e8400-e29b-41d4-a716-446655440000",
 *   "email": "user@example.com",
 *   "preferences": ["Fiction", "Science"],
 *   "annualGoal": 12,
 *   "iat": 1713000000,
 *   "exp": 1713003600
 * }
 * 
 * And frontend will automatically extract user info:
 * 
 * AuthService.login() 
 *   → POST /api/auth/login
 *   → Receive { accessToken, refreshToken }
 *   → JwtDecoderService.decode(accessToken)
 *   → Extract { userId, email, preferences, annualGoal }
 *   → Create User object
 *   → Store in auth.store + localStorage
 */
