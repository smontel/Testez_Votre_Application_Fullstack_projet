package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testUserCreation() {
        // Given
        User newUser = User.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // Then
        assertNotNull(newUser);
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("password123", newUser.getPassword());
        assertFalse(newUser.isAdmin());
    }

    @Test
    void testUserWithAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = new User(1L, "test@example.com", "Doe", "John", "password123", false, now, now);

        // Then
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("password123", user.getPassword());
        assertFalse(user.isAdmin());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testUserWithRequiredArgsConstructor() {
        // Given
        User user = new User("test@example.com", "Doe", "John", "password123", false);

        // Then
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("password123", user.getPassword());
        assertFalse(user.isAdmin());
        assertNull(user.getId());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        // When
        user.setId(1L);
        user.setEmail("john.doe@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("securePassword");
        user.setAdmin(true);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(updatedAt);

        // Then
        assertEquals(1L, user.getId());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("securePassword", user.getPassword());
        assertTrue(user.isAdmin());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    void testFluentSetters() {
        // Given & When
        User user = new User()
                .setEmail("fluent@example.com")
                .setFirstName("Jane")
                .setLastName("Smith")
                .setPassword("fluentPassword")
                .setAdmin(true);

        // Then
        assertEquals("fluent@example.com", user.getEmail());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("fluentPassword", user.getPassword());
        assertTrue(user.isAdmin());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        User user3 = new User();
        user3.setId(2L);

        // Then
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testEqualsWithNullId() {
        // Given
        User user1 = new User();
        User user2 = new User();

        // Then
        assertEquals(user1, user2); // Both have null id
    }

    @Test
    void testToString() {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password123")
                .admin(false)
                .build();

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
    }

    @Test
    void testAdminField() {
        // Given
        User adminUser = new User();
        User regularUser = new User();

        // When
        adminUser.setAdmin(true);
        regularUser.setAdmin(false);

        // Then
        assertTrue(adminUser.isAdmin());
        assertFalse(regularUser.isAdmin());
    }

    @Test
    void testNoArgsConstructor() {
        // When
        User user = new User();

        // Then
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getPassword());
        assertFalse(user.isAdmin()); // primitive boolean defaults to false
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }
}