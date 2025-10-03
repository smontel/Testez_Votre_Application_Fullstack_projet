package com.openclassrooms.starterjwt.controller;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user1;
    private User user2;
    private User user3;
    private Session session;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        // Créer et sauvegarder un professeur
        teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");
        teacher = teacherRepository.save(teacher);

        // Créer et sauvegarder des utilisateurs
        user1 = new User();
        user1.setEmail("test1@studio.com");
        user1.setFirstName("Patrick");
        user1.setLastName("BERTRAND");
        user1.setPassword("$2a$10$Dwn5XvJVcKOSuGBF9zTMuOOGwLEH2Hw5T0NxNjc5JrPRKccJeUAzy"); // test1234!
        user1.setAdmin(false);
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setEmail("test2@studio.com");
        user2.setFirstName("Marie");
        user2.setLastName("DUPONT");
        user2.setPassword("$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq");
        user2.setAdmin(false);
        user2 = userRepository.save(user2);

        user3 = new User();
        user3.setEmail("test3@studio.com");
        user3.setFirstName("Jean");
        user3.setLastName("GASTON");
        user3.setPassword("$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq");
        user3.setAdmin(false);
        user3 = userRepository.save(user3);

        // Créer et sauvegarder une session
        session = new Session();
        session.setName("Test Session");
        session.setDescription("Test description");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(new ArrayList<>(Arrays.asList(user1, user2)));
        session = sessionRepository.save(session);
    }
    //GetSessionById
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnSessionById() throws Exception {
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Test Session"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0]").value(user1.getId()))
                .andExpect(jsonPath("$.users[1]").value(user2.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnNotFoundWhenSessionNotExistById() throws Exception{
        mockMvc.perform(get("/api/session/753"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestWhenSessionIdInvalid() throws Exception{
        mockMvc.perform(get("/api/session/invalid-id"))
                .andExpect(status().isBadRequest());
    }
    //GetAllSession
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnAllSessions() throws Exception {
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(session.getId()));
    }

    //Create
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldCreateSession() throws Exception {
        String jsonContent = String.format("{"
                + "\"name\":\"Test Session 2\","
                + "\"description\":\"Test description 2\","
                + "\"date\":\"2025-12-25T10:30:00\","
                + "\"teacher_id\":%d,"
                + "\"users\":[%d,%d]"
                + "}", teacher.getId(), user1.getId(), user2.getId());

        mockMvc.perform(post("/api/session").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Session 2"))
                .andExpect(jsonPath("$.description").value("Test description 2"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0]").value(user1.getId()))
                .andExpect(jsonPath("$.users[1]").value(user2.getId()));
    }


    //Update
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldUpdateSession() throws Exception {
        String jsonContent = String.format("{"
                + "\"name\":\"Test Session Updated\","
                + "\"description\":\"Test description Updated\","
                + "\"date\":\"2025-12-25T10:30:00\","
                + "\"teacher_id\":%d,"
                + "\"users\":[%d,%d]"
                + "}", teacher.getId(), user1.getId(), user2.getId());

        mockMvc.perform(put("/api/session/"+session.getId()).contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(session.getId()))
                .andExpect(jsonPath("$.name").value("Test Session Updated"))
                .andExpect(jsonPath("$.description").value("Test description Updated"))
                .andExpect(jsonPath("$.teacher_id").value(teacher.getId()))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0]").value(user1.getId()))
                .andExpect(jsonPath("$.users[1]").value(user2.getId()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestWhenIdInvalid() throws Exception{
        String jsonContent = String.format("{"
                + "\"name\":\"Test Session Updated\","
                + "\"description\":\"Test description Updated\","
                + "\"date\":\"2025-12-25T10:30:00\","
                + "\"teacher_id\":%d,"
                + "\"users\":[%d,%d]"
                + "}", teacher.getId(), user1.getId(), user2.getId());

        mockMvc.perform(put("/api/session/invalidId").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andExpect(status().isBadRequest());
    }


    //Delete
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldDeleteSession() throws Exception {
        Long sessionId = session.getId();
        assertTrue(sessionRepository.findById(sessionId).isPresent());
        mockMvc.perform(delete("/api/session/" + sessionId))
                .andExpect(status().isOk());
        assertFalse(sessionRepository.findById(sessionId).isPresent());

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestWhenDeletingWithInvalidId() throws Exception {
        // ID invalide (pas un nombre)
        mockMvc.perform(delete("/api/session/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnNotFoundWhenDeletingWithInvalidId() throws Exception {
        // ID invalide (pas d'utilisateur)
        mockMvc.perform(delete("/api/session/25"))
                .andExpect(status().isNotFound());
    }



    //Participate
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @Transactional
    void shouldParticipateToSession() throws Exception {
        Long sessionId = session.getId();
        assertFalse(session.getUsers().contains(user3));

        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/"+user3.getId()))
                .andExpect(status().isOk());

        Session sessionUpdated = sessionRepository.getById(sessionId);
        assertTrue(sessionUpdated.getUsers().contains(user3));

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestWhenParticipatingWithInvalidId() throws Exception {
        assertFalse(session.getUsers().contains(user3));

        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestWhenParticipatingAlready() throws Exception {
        assertTrue(session.getUsers().contains(user2));

        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/"+user2.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnNotFoundTWhenParticipateNonExistingSession() throws Exception {
        assertTrue(session.getUsers().contains(user2));

        mockMvc.perform(post("/api/session/753/participate/"+user2.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnNotFoundTWhenParticipateNonExistingUser() throws Exception {
        assertTrue(session.getUsers().contains(user2));

        mockMvc.perform(post("/api/session/"+session.getId()+"/participate/753"))
                .andExpect(status().isNotFound());
    }



    //Unparticipate
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @Transactional
    void shouldUnparticipateFromSession() throws Exception {
        assertTrue(session.getUsers().contains(user2));

        mockMvc.perform(delete("/api/session/"+session.getId()+"/participate/"+user2.getId()))
                .andExpect(status().isOk());

        Session sessionUpdated = sessionRepository.getById(session.getId());
        assertFalse(sessionUpdated.getUsers().contains(user2));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    @Transactional
    void shouldReturnBadRequestWhenUnparticipatingWithInvalidId() throws Exception {
        assertTrue(session.getUsers().contains(user2));

        mockMvc.perform(delete("/api/session/"+session.getId()+"/participate/invalid-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnBadRequestToUnparticipateWhenUserNotParticipating() throws Exception {
        assertFalse(session.getUsers().contains(user3));

        mockMvc.perform(delete("/api/session/"+session.getId()+"/participate/"+user3.getId()))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnNotFoundTWhenUnparticipateNonExistingSession() throws Exception {
        mockMvc.perform(delete("/api/session/753/participate/"+user3.getId()))
                .andExpect(status().isNotFound());
    }


}