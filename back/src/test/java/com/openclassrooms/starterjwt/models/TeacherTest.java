package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    private Teacher teacher;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
    }

    @Test
    void testTeacherCreation() {
        // Given
        Teacher newTeacher = Teacher.builder()
                .firstName("Marie")
                .lastName("Dupont")
                .build();

        // Then
        assertNotNull(newTeacher);
        assertEquals("Marie", newTeacher.getFirstName());
        assertEquals("Dupont", newTeacher.getLastName());
    }

    @Test
    void testTeacherWithAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = new Teacher(1L, "Martin", "Pierre", now, now);

        // Then
        assertEquals(1L, teacher.getId());
        assertEquals("Martin", teacher.getLastName());
        assertEquals("Pierre", teacher.getFirstName());
        assertEquals(now, teacher.getCreatedAt());
        assertEquals(now, teacher.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        // When
        teacher.setId(1L);
        teacher.setFirstName("Sophie");
        teacher.setLastName("Bernard");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        teacher.setCreatedAt(createdAt);
        teacher.setUpdatedAt(updatedAt);

        // Then
        assertEquals(1L, teacher.getId());
        assertEquals("Sophie", teacher.getFirstName());
        assertEquals("Bernard", teacher.getLastName());
        assertEquals(createdAt, teacher.getCreatedAt());
        assertEquals(updatedAt, teacher.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);

        Teacher teacher2 = new Teacher();
        teacher2.setId(1L);

        Teacher teacher3 = new Teacher();
        teacher3.setId(2L);

        // Then
        assertEquals(teacher1, teacher2);
        assertNotEquals(teacher1, teacher3);
        assertEquals(teacher1.hashCode(), teacher2.hashCode());
        assertNotEquals(teacher1.hashCode(), teacher3.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        // Given
        Teacher teacher1 = new Teacher();
        Teacher teacher2 = new Teacher();

        // Then
        assertEquals(teacher1, teacher2); // Both have null id
    }

    @Test
    void testEqualsWithSameIdDifferentNames() {
        // Given
        Teacher teacher1 = new Teacher();
        teacher1.setId(1L);
        teacher1.setFirstName("Alice");
        teacher1.setLastName("Dupond");

        Teacher teacher2 = new Teacher();
        teacher2.setId(1L);
        teacher2.setFirstName("Bob");
        teacher2.setLastName("Martin");

        // Then
        assertEquals(teacher1, teacher2); // Equals only checks ID
    }

    @Test
    void testToString() {
        // Given
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Antoine")
                .lastName("Rousseau")
                .build();

        // When
        String toString = teacher.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Teacher"));
        assertTrue(toString.contains("Antoine"));
        assertTrue(toString.contains("Rousseau"));
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Teacher teacher = new Teacher();

        // Then
        assertNotNull(teacher);
        assertNull(teacher.getId());
        assertNull(teacher.getFirstName());
        assertNull(teacher.getLastName());
        assertNull(teacher.getCreatedAt());
        assertNull(teacher.getUpdatedAt());
    }

    @Test
    void testBuilderWithPartialData() {
        // Given & When
        Teacher teacher = Teacher.builder()
                .firstName("Emma")
                .build();

        // Then
        assertEquals("Emma", teacher.getFirstName());
        assertNull(teacher.getLastName());
        assertNull(teacher.getId());
    }

    @Test
    void testBuilderWithAllData() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 2, 1, 10, 0);

        // When
        Teacher teacher = Teacher.builder()
                .id(5L)
                .firstName("Lucas")
                .lastName("Leroy")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Then
        assertEquals(5L, teacher.getId());
        assertEquals("Lucas", teacher.getFirstName());
        assertEquals("Leroy", teacher.getLastName());
        assertEquals(createdAt, teacher.getCreatedAt());
        assertEquals(updatedAt, teacher.getUpdatedAt());
    }

    @Test
    void testDateFields() {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2023, 5, 15, 14, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 6, 20, 16, 45);

        // When
        teacher.setCreatedAt(createdAt);
        teacher.setUpdatedAt(updatedAt);

        // Then
        assertEquals(createdAt, teacher.getCreatedAt());
        assertEquals(updatedAt, teacher.getUpdatedAt());
    }

    @Test
    void testFluentChaining() {
        // Given & When
        Teacher teacher = new Teacher()
                .setId(10L)
                .setFirstName("Camille")
                .setLastName("Petit")
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        // Then
        assertNotNull(teacher);
        assertEquals(10L, teacher.getId());
        assertEquals("Camille", teacher.getFirstName());
        assertEquals("Petit", teacher.getLastName());
        assertNotNull(teacher.getCreatedAt());
        assertNotNull(teacher.getUpdatedAt());
    }
}