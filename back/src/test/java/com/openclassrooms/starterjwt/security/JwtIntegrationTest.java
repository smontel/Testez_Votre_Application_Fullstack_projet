package com.openclassrooms.starterjwt.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

@SpringBootTest
@AutoConfigureMockMvc
class JwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetailsImpl userDetails;
    private String validToken;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("yoga@studio.com")
                .firstName("Yoga")
                .lastName("Studio")
                .password("password")
                .build();

        // Génération d'un token valide pour les tests
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        validToken = jwtUtils.generateJwtToken(authentication);
    }

    @Test
    void testCompleteJwtFlow() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act - Génération du token
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert - Validation du token
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();

        // Assert - Extraction des informations
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertThat(username).isEqualTo("yoga@studio.com");
    }

    @Test
    void testAuthenticationWithValidBearerToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());
    }

    @Test
    void testRejectionWithoutAuthorizationHeader() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRejectionWithInvalidToken() throws Exception {
        // Arrange
        String invalidToken = "invalid.jwt.token.here";

        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRejectionWithExpiredToken() throws Exception {
        // Arrange - Token expiré (créé il y a 2 jours, expiré hier)
        String expiredToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 172800000)) // -2 jours
                .setExpiration(new Date(System.currentTimeMillis() - 86400000)) // -1 jour
                .signWith(SignatureAlgorithm.HS512, "testSecretKey")
                .compact();

        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRejectionWithMalformedBearerHeader() throws Exception {
        // Act & Assert - Sans "Bearer " prefix
        mockMvc.perform(get("/api/session")
                        .header("Authorization", validToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRejectionWithWrongSignature() throws Exception {
        // Arrange - Token signé avec une mauvaise clé
        String wrongSignatureToken = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        // Act & Assert
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + wrongSignatureToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAllJwtValidationExceptions() {
        // Test 1: SignatureException
        String tokenWrongSignature = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SignatureAlgorithm.HS512, "wrongSecret")
                .compact();
        assertThat(jwtUtils.validateJwtToken(tokenWrongSignature)).isFalse();

        // Test 2: MalformedJwtException
        assertThat(jwtUtils.validateJwtToken("malformed.token.here")).isFalse();

        // Test 3: ExpiredJwtException
        String expiredToken = Jwts.builder()
                .setSubject("test@test.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(SignatureAlgorithm.HS512, "testSecretKey")
                .compact();
        assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();

        // Test 4: UnsupportedJwtException
        String unsupportedToken = Jwts.builder()
                .setSubject("test@test.com")
                .compact();
        assertThat(jwtUtils.validateJwtToken(unsupportedToken)).isFalse();

        // Test 5: IllegalArgumentException (empty token)
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
        assertThat(jwtUtils.validateJwtToken(null)).isFalse();
    }

    @Test
    void testUsernameExtractionFromToken() {
        // Act
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(validToken);

        // Assert
        assertThat(extractedUsername).isEqualTo("yoga@studio.com");
    }

    @Test
    void testAuthenticationPersistenceAcrossRequests() throws Exception {
        // Request 1
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        // Request 2 - Le même token devrait toujours fonctionner
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk());

        // Request 3 - Sans token devrait échouer
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testTokenPropertiesValidation() {
        // Arrange
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // Act
        String token = jwtUtils.generateJwtToken(authentication);

        // Assert - Le token doit être valide
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();

        // Assert - Le token doit contenir le bon username
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("yoga@studio.com");

        // Assert - Le token ne doit pas être vide
        assertThat(token).hasSizeGreaterThan(50);
    }
}