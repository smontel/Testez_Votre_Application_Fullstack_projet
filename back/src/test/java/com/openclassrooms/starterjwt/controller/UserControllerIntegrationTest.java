package com.openclassrooms.starterjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private User testUser1;
    private User testUser2;
    private User adminUser;

    private final String userName = "testuser@studio.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser1 = new User();
        testUser1.setEmail(userName);
        testUser1.setFirstName("John");
        testUser1.setLastName("Doe");
        testUser1.setPassword(passwordEncoder.encode("password1234!"));
        testUser1.setAdmin(false);
        userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setEmail("testuser2@studio.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Does");
        testUser2.setPassword(passwordEncoder.encode("password1234"));
        testUser2.setAdmin(false);
        userRepository.save(testUser2);

        adminUser = new User();
        adminUser.setEmail("admin@studio.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("ADMIN");
        adminUser.setPassword(passwordEncoder.encode("adminPassword"));
        adminUser.setAdmin(true);
        userRepository.save(adminUser);
    }

    // GET

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldFindByIdSuccess() throws Exception {
         mockMvc.perform(get("/api/user/"+testUser1.getId()))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.id").value(testUser1.getId()))
                 .andExpect(jsonPath("$.email").value(testUser1.getEmail()))
                 .andExpect(jsonPath("$.firstName").value(testUser1.getFirstName()))
                 .andExpect(jsonPath("$.lastName").value(testUser1.getLastName()))
                 .andExpect(jsonPath("$.admin").value(testUser1.isAdmin()));
    }

    @Test
    @WithMockUser
    void shouldFindByIdNotFound() throws Exception {

         mockMvc.perform(get("/api/user/777"))
                 .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldFindByIdInvalidFormat() throws Exception {

         mockMvc.perform(get("/api/user/invalid-id}"))
                 .andExpect(status().isBadRequest());
    }

//Delete
    @Test
    @WithMockUser(username = userName, roles = {"USER"})
    void shouldDeleteOwnAccountSuccess() throws Exception {
         mockMvc.perform(delete("/api/user/"+testUser1.getId()))
                 .andExpect(status().isOk());

         assertFalse(userRepository.existsById(testUser1.getId()));
    }

    @Test
    @WithMockUser(username = userName, roles = {"USER"})
    void shouldDeleteOtherUserAccountUnauthorized() throws Exception {

         mockMvc.perform(delete("/api/user/"+testUser2.getId()))
                 .andExpect(status().isUnauthorized());

         assertTrue(userRepository.existsById(testUser2.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldDeleteNonExistentUser() throws Exception {
         mockMvc.perform(delete("/api/user/773"))
                 .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteWithInvalidIdFormat() throws Exception {

         mockMvc.perform(delete("/api/user/invalid-id"))
                 .andExpect(status().isBadRequest());
    }

    // Tests de sécurité

    @Test
    void testFindByIdWithoutAuthentication() throws Exception {

         mockMvc.perform(get("/api/user/"+testUser1.getId()))
                 .andExpect(status().isUnauthorized());
    }

//    @Test
//    void testDeleteWithoutAuthentication() throws Exception {
//
//         mockMvc.perform(delete("/api/user/"+testUser1.getId()))
//                 .andExpect(status().isUnauthorized());
//    }


}