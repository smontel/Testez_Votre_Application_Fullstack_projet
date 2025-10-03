package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private Session session1;
    private Session session2;
    private Session session3;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        teacherRepository.deleteAll();

        Teacher teacher = new Teacher();
        teacher.setFirstName("Margot");
        teacher.setLastName("DELAHAYE");
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());
        teacherRepository.save(teacher);

        session1 = new Session();
        session1.setName("Session test 1");
        session1.setDescription("Test de la session numero 1");
        session1.setDate(Date.valueOf("2026-07-26"));
        session1.setTeacher(teacher);
        session1.setCreatedAt(LocalDateTime.now());
        session1.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session1);

        session2 = new Session();
        session2.setName("Session test 2");
        session2.setDescription("Test de la session numero 2");
        session2.setDate(Date.valueOf("2026-07-27"));
        session2.setTeacher(teacher);
        session2.setCreatedAt(LocalDateTime.now());
        session2.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session2);

        session3 = new Session();
        session3.setName("Session test 3");
        session3.setDescription("Test de la session numero 3");
        session3.setDate(Date.valueOf("2026-07-28"));
        session3.setTeacher(teacher);
        session3.setCreatedAt(LocalDateTime.now());
        session3.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session3);
    }

    @Test
    void shouldGetAllSession() {
        List<Session> sessions = sessionRepository.findAll();

        assertEquals(3, sessions.size());
        assertEquals("Session test 1", sessions.get(0).getName());
    }

    @Test
    void shouldGetSessionById() {
        Session session = sessionRepository.getById(session1.getId());

        assertEquals("Session test 1", session.getName());
        assertEquals("Test de la session numero 1", session.getDescription());
    }

    @Test
    void shouldSaveSession() {
        Session session = new Session();
        session.setName("session test");
        session.setDate(Date.valueOf("2026-07-26"));
        session.setDescription("description session1");

        Session savedSession = sessionRepository.save(session);

        assertNotNull(savedSession.getId());
        assertEquals("session test", savedSession.getName());
    }

    @Test
    void shouldUpdateSession() {
        Session session = sessionRepository.getById(session1.getId());
        session.setName("Session 1 updated");

        Session updatedSession = sessionRepository.save(session);

        assertEquals("Session 1 updated", updatedSession.getName());
    }

    @Test
    void shouldDeleteSession() {
        sessionRepository.deleteById(session3.getId());
        Optional<Session> session = sessionRepository.findById(session3.getId());
        assertFalse(session.isPresent());
    }
}