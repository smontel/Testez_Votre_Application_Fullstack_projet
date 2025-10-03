package com.openclassrooms.starterjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Données de test
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test1@studio.com");
        testUser.setFirstName("Patrick");
        testUser.setLastName("BERTRAND");
        testUser.setPassword(passwordEncoder.encode("test1234!")); // test1234!
        testUser.setAdmin(false);
        userRepository.save(testUser);

        adminUser = new User();
        adminUser.setEmail("admin@studio.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("ADMIN");
        adminUser.setPassword(passwordEncoder.encode("test12345!")); // test1234!
        adminUser.setAdmin(true);
        userRepository.save(adminUser);
    }

    @Test
    void testLoginSuccess() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail(testUser.getEmail());
         loginRequest.setPassword("test1234!");

         String jsonContent = objectMapper.writeValueAsString(loginRequest);

         mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.token", notNullValue()))
                 .andExpect(jsonPath("$.type").value("Bearer"))
                 .andExpect(jsonPath("$.id", notNullValue()))
                 .andExpect(jsonPath("$.username").value(testUser.getEmail()))
                 .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                 .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                 .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void testLoginWithAdminUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(adminUser.getEmail());
        loginRequest.setPassword("test12345!");

        String jsonContent = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username").value(adminUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(adminUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(adminUser.getLastName()))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail(testUser.getEmail());
         loginRequest.setPassword("wrongPassword");

         String jsonContent = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithNonExistentUser() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail("nonexistent@example.com");
         loginRequest.setPassword("somePassword");

         String jsonContent = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginWithInvalidRequestBody() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail("");
         loginRequest.setPassword("");

         String jsonContent = objectMapper.writeValueAsString(loginRequest);

         mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterSuccess() throws Exception {

         SignupRequest signupRequest = new SignupRequest();
         signupRequest.setEmail("newuser@example.com");
         signupRequest.setFirstName("Jane");
         signupRequest.setLastName("Smith");
         signupRequest.setPassword("newPassword123");

         String jsonContent = objectMapper.writeValueAsString(signupRequest);

         mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.message").value("User registered successfully!"));


         assertTrue(userRepository.existsByEmail("newuser@example.com"));
    }

    @Test
    void testRegisterWithExistingEmail() throws Exception {

         SignupRequest signupRequest = new SignupRequest();
         signupRequest.setEmail(testUser.getEmail());
         signupRequest.setFirstName("Jane");
         signupRequest.setLastName("Smith");
         signupRequest.setPassword("newPassword123");

         String jsonContent = objectMapper.writeValueAsString(signupRequest);

         mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isBadRequest())
                 .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
    }

    @Test
    void testRegisterWithInvalidData() throws Exception {
        // TODO: Testez un enregistrement avec des données invalides

         SignupRequest signupRequest = new SignupRequest();
         signupRequest.setEmail("");
         signupRequest.setFirstName("");
         signupRequest.setLastName("");
         signupRequest.setPassword("");

         String jsonContent = objectMapper.writeValueAsString(signupRequest);

         mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                 .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterWithInvalidEmailFormat() throws Exception {
        // TODO: Testez avec un format d'email invalide

        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("yogastudio.com");
        signupRequest.setFirstName("Jean");
        signupRequest.setLastName("DUPONT");
        signupRequest.setPassword("newPassword123");

        String jsonContent = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPasswordIsEncodedAfterRegistration() throws Exception {

         String rawPassword = "testPassword123";
         SignupRequest signupRequest = new SignupRequest();
         signupRequest.setEmail("password-test@example.com");
         signupRequest.setFirstName("Test");
         signupRequest.setLastName("User");
         signupRequest.setPassword(rawPassword);

         String jsonContent = objectMapper.writeValueAsString(signupRequest);

         mockMvc.perform(post("/api/auth/register")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(jsonContent))
                 .andExpect(status().isOk());

         User savedUser = userRepository.findByEmail("password-test@example.com").orElse(null);
         assertNotNull(savedUser);
         assertNotEquals(rawPassword, savedUser.getPassword());
         assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()));
    }

}