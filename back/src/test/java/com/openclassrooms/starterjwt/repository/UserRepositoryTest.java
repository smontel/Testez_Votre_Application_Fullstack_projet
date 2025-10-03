package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        adminUser = new User();
        adminUser.setEmail("yoga@studio.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("Admin");
        adminUser.setPassword("password123");
        adminUser.setAdmin(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(adminUser);

        regularUser = new User();
        regularUser.setEmail("user@studio.com");
        regularUser.setFirstName("NoAdmin");
        regularUser.setLastName("User");
        regularUser.setPassword("password123");
        regularUser.setAdmin(false);
        regularUser.setCreatedAt(LocalDateTime.now());
        regularUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(regularUser);
    }

    @Test
    void shouldGetAllUser() {
        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertEquals("NoAdmin", users.get(1).getFirstName());
    }

    @Test
    void shouldGetUserById() {
        User user = userRepository.getById(adminUser.getId());

        assertEquals("Admin", user.getFirstName());
        assertEquals("Admin", user.getLastName());
        assertEquals("yoga@studio.com", user.getEmail());
    }

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setFirstName("testUser");
        user.setLastName("testUserLastName");
        user.setEmail("test@studio.com");
        user.setAdmin(false);
        user.setPassword("password");

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("testUser", savedUser.getFirstName());
    }

    @Test
    void shouldUpdateUser() {
        User user = userRepository.getById(regularUser.getId());

        user.setFirstName("UpdatedName");

        User updatedUser = userRepository.save(user);

        assertEquals("UpdatedName", updatedUser.getFirstName());
    }

    @Test
    void shouldDeleteUser() {
        userRepository.deleteById(regularUser.getId());
        Optional<User> user = userRepository.findById(regularUser.getId());
        assertFalse(user.isPresent());
    }
}