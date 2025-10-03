package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    private Session session;
    private Teacher teacher;
    private User user1;
    private User user2;
    private Date sessionDate;

    @BeforeEach
    void setUp() {
        session = new Session();

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("Marie");
        teacher.setLastName("Dupont");

        user1 = new User();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setFirstName("John");
        user1.setLastName("Doe");

        user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");

        sessionDate = new Date();
    }

    @Test
    void testSessionCreation() {
        Session newSession = Session.builder()
                .name("Yoga matinal")
                .date(sessionDate)
                .description("Session de yoga pour bien commencer la journée")
                .teacher(teacher)
                .build();

        assertNotNull(newSession);
        assertEquals("Yoga matinal", newSession.getName());
        assertEquals(sessionDate, newSession.getDate());
        assertEquals("Session de yoga pour bien commencer la journée", newSession.getDescription());
        assertEquals(teacher, newSession.getTeacher());
    }

    @Test
    void testSessionWithAllArgsConstructor() {
        List<User> users = Arrays.asList(user1, user2);
        LocalDateTime now = LocalDateTime.now();

        Session session = new Session(1L, "Pilates", sessionDate, "Cours de pilates", teacher, users, now, now);

        assertEquals(1L, session.getId());
        assertEquals("Pilates", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Cours de pilates", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
        assertEquals(now, session.getCreatedAt());
        assertEquals(now, session.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        session.setId(1L);
        session.setName("Meditation");
        session.setDate(sessionDate);
        session.setDescription("Session de méditation guidée");
        session.setTeacher(teacher);
        List<User> users = Arrays.asList(user1);
        session.setUsers(users);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(updatedAt);

        assertEquals(1L, session.getId());
        assertEquals("Meditation", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Session de méditation guidée", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(updatedAt, session.getUpdatedAt());
    }

    @Test
    void testFluentSetters() {
        List<User> users = Arrays.asList(user1, user2);
        Session session = new Session()
                .setName("Stretching")
                .setDate(sessionDate)
                .setDescription("Session d'étirements")
                .setTeacher(teacher)
                .setUsers(users);

        assertEquals("Stretching", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Session d'étirements", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
    }

    @Test
    void testEqualsAndHashCode() {
        Session session1 = new Session();
        session1.setId(1L);

        Session session2 = new Session();
        session2.setId(1L);

        Session session3 = new Session();
        session3.setId(2L);

        assertEquals(session1, session2);
        assertNotEquals(session1, session3);
        assertEquals(session1.hashCode(), session2.hashCode());
        assertNotEquals(session1.hashCode(), session3.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        Session session1 = new Session();
        Session session2 = new Session();

        assertEquals(session1, session2);
    }

    @Test
    void testEqualsWithSameIdDifferentData() {
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Session 1");

        Session session2 = new Session();
        session2.setId(1L);
        session2.setName("Session 2");

        assertEquals(session1, session2); // Equals only checks ID
    }

    @Test
    void testToString() {
        Session session = Session.builder()
                .id(1L)
                .name("Yoga avancé")
                .date(sessionDate)
                .description("Session de yoga pour pratiquants avancés")
                .teacher(teacher)
                .build();

        String toString = session.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Session"));
        assertTrue(toString.contains("Yoga avancé"));
    }

    @Test
    void testNoArgsConstructor() {
        Session session = new Session();

        assertNotNull(session);
        assertNull(session.getId());
        assertNull(session.getName());
        assertNull(session.getDate());
        assertNull(session.getDescription());
        assertNull(session.getTeacher());
        assertNull(session.getUsers());
        assertNull(session.getCreatedAt());
        assertNull(session.getUpdatedAt());
    }

    @Test
    void testTeacherAssociation() {
        session.setTeacher(teacher);

        assertEquals(teacher, session.getTeacher());
        assertEquals(1L, session.getTeacher().getId());
        assertEquals("Marie", session.getTeacher().getFirstName());
        assertEquals("Dupont", session.getTeacher().getLastName());
    }

    @Test
    void testUsersListAssociation() {
        List<User> users = Arrays.asList(user1, user2);

        session.setUsers(users);

        assertEquals(users, session.getUsers());
        assertEquals(2, session.getUsers().size());
        assertTrue(session.getUsers().contains(user1));
        assertTrue(session.getUsers().contains(user2));
    }


    @Test
    void testBuilderWithPartialData() {
        // Given & When
        Session session = Session.builder()
                .name("Session partielle")
                .date(sessionDate)
                .build();

        // Then
        assertEquals("Session partielle", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertNull(session.getDescription());
        assertNull(session.getTeacher());
        assertNull(session.getUsers());
    }

    @Test
    void testBuilderWithAllData() {
        // Given
        List<User> users = Arrays.asList(user1, user2);
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 2, 1, 10, 0);

        // When
        Session session = Session.builder()
                .id(5L)
                .name("Session complète")
                .date(sessionDate)
                .description("Description complète de la session")
                .teacher(teacher)
                .users(users)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Then
        assertEquals(5L, session.getId());
        assertEquals("Session complète", session.getName());
        assertEquals(sessionDate, session.getDate());
        assertEquals("Description complète de la session", session.getDescription());
        assertEquals(teacher, session.getTeacher());
        assertEquals(users, session.getUsers());
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(updatedAt, session.getUpdatedAt());
    }

    @Test
    void testDateFields() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 15, 14, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 6, 20, 16, 45);

        // When
        session.setCreatedAt(createdAt);
        session.setUpdatedAt(updatedAt);

        // Then
        assertEquals(createdAt, session.getCreatedAt());
        assertEquals(updatedAt, session.getUpdatedAt());
    }



}