package com.openclassrooms.starterjwt.security.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;

@SpringBootTest
@DisplayName("UserDetailsService Unit Tests")
class UserDetailsServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User()
                .setId(1L)
                .setEmail("yoga@studio.com")
                .setLastName("Studio")
                .setFirstName("Yoga")
                .setPassword("hashedPassword123")
                .setAdmin(false)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void loadUserByUsername_withValidEmail_shouldReturnUserDetails() {
        // Arrange
        String email = "yoga@studio.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(email);
        assertThat(userDetails.getPassword()).isEqualTo("hashedPassword123");

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_withInvalidEmail_shouldThrowException() {
        // Arrange
        String email = "nonexistent@studio.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: " + email);

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should map all user fields correctly to UserDetails")
    void loadUserByUsername_shouldMapAllFieldsCorrectly() {
        // Arrange
        String email = "yoga@studio.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
        assertThat(userDetailsImpl.getId()).isEqualTo(1L);
        assertThat(userDetailsImpl.getUsername()).isEqualTo("yoga@studio.com");
        assertThat(userDetailsImpl.getFirstName()).isEqualTo("Yoga");
        assertThat(userDetailsImpl.getLastName()).isEqualTo("Studio");
        assertThat(userDetailsImpl.getPassword()).isEqualTo("hashedPassword123");
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void loadUserByUsername_withNullEmail_shouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(null))
                .isInstanceOf(UsernameNotFoundException.class);

        verify(userRepository).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle empty email gracefully")
    void loadUserByUsername_withEmptyEmail_shouldThrowException() {
        // Arrange
        String emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User Not Found with email: ");

        verify(userRepository).findByEmail(emptyEmail);
    }
}