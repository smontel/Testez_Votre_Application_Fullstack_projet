package com.openclassrooms.starterjwt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SessionRepository sessionRepository;

    // Données de test
    private Teacher teacher1;
    private Teacher teacher2;
    private List<Teacher> testTeachers;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher1 = new Teacher();
        teacher1.setFirstName("John");
        teacher1.setLastName("Doe");
        teacher1 = teacherRepository.save(teacher1);


        teacher2 = new Teacher();
        teacher2.setFirstName("Jane");
        teacher2.setLastName("Smith");
        teacher2 = teacherRepository.save(teacher2);

        testTeachers = Arrays.asList(teacher1, teacher2);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByIdSuccess() throws Exception {
         mockMvc.perform(get("/api/teacher/"+teacher1.getId()))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.id").value(teacher1.getId()))
                 .andExpect(jsonPath("$.firstName").value(teacher1.getFirstName()))
                 .andExpect(jsonPath("$.lastName").value(teacher1.getLastName()));
    }

    @Test
    @WithMockUser
    void testFindByIdNotFound() throws Exception {
         mockMvc.perform(get("/api/teacher/723"))
                 .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testFindByIdInvalidFormat() throws Exception {
        mockMvc.perform(get("/api/teacher/invalid-id"))
                 .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void testFindAllSuccess() throws Exception {
        mockMvc.perform(get("/api/teacher"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.length()").value(2))
                 .andExpect(jsonPath("$[0].id").value(teacher1.getId()))
                 .andExpect(jsonPath("$[0].firstName").value(teacher1.getFirstName()))
                 .andExpect(jsonPath("$[0].lastName").value(teacher1.getLastName()))
                 .andExpect(jsonPath("$[1].id").value(teacher2.getId()))
                 .andExpect(jsonPath("$[1].firstName").value(teacher2.getFirstName()))
                 .andExpect(jsonPath("$[1].lastName").value(teacher2.getLastName()));
    }

    @Test
    @WithMockUser
    void testFindAllEmptyList() throws Exception {
         teacherRepository.deleteAll();

         mockMvc.perform(get("/api/teacher"))
                 .andDo(print())
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.length()").value(0));
    }
}