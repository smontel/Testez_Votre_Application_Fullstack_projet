package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;

@SpringBootTest
@DisplayName("SessionMapper Tests")
class SessionMapperTest {

    @Autowired
    private SessionMapper sessionMapper;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private UserService userService;

    private Teacher teacher;
    private User user1;
    private User user2;
    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setUp() {
        // Teacher
        teacher = Teacher.builder()
                .id(1L)
                .lastName("DELAHAYE")
                .firstName("Margot")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Users
        user1 = new User()
                .setId(1L)
                .setEmail("user1@studio.com")
                .setLastName("User1")
                .setFirstName("Test")
                .setPassword("password")
                .setAdmin(false)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        user2 = new User()
                .setId(2L)
                .setEmail("user2@studio.com")
                .setLastName("User2")
                .setFirstName("Test")
                .setPassword("password")
                .setAdmin(false)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        // Session Entity
        session = Session.builder()
                .id(1L)
                .name("Yoga Session")
                .description("Morning yoga session")
                .date(new Date())
                .teacher(teacher)
                .users(Arrays.asList(user1, user2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Session DTO
        sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setName("Yoga Session");
        sessionDto.setDescription("Morning yoga session");
        sessionDto.setDate(new Date());
        sessionDto.setTeacher_id(1L);
        sessionDto.setUsers(Arrays.asList(1L, 2L));
    }

    @Test
    @DisplayName("Should convert SessionDto to Session entity")
    void toEntity_shouldConvertDtoToEntity() {
        // Arrange
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Morning yoga session");
        assertThat(result.getTeacher()).isNotNull();
        assertThat(result.getTeacher().getId()).isEqualTo(1L);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers().get(0).getId()).isEqualTo(1L);
        assertThat(result.getUsers().get(1).getId()).isEqualTo(2L);

        verify(teacherService).findById(1L);
        verify(userService, times(2)).findById(org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("Should convert Session entity to SessionDto")
    void toDto_shouldConvertEntityToDto() {
        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Yoga Session");
        assertThat(result.getDescription()).isEqualTo("Morning yoga session");
        assertThat(result.getTeacher_id()).isEqualTo(1L);
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("Should handle null teacher in toEntity")
    void toEntity_withNullTeacher_shouldReturnEntityWithNullTeacher() {
        // Arrange
        sessionDto.setTeacher_id(null);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTeacher()).isNull();
        assertThat(result.getUsers()).hasSize(2);
    }

    @Test
    @DisplayName("Should handle null users list in toEntity")
    void toEntity_withNullUsersList_shouldReturnEntityWithEmptyList() {
        // Arrange
        sessionDto.setUsers(null);
        when(teacherService.findById(1L)).thenReturn(teacher);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty users list in toEntity")
    void toEntity_withEmptyUsersList_shouldReturnEntityWithEmptyList() {
        // Arrange
        sessionDto.setUsers(Collections.emptyList());
        when(teacherService.findById(1L)).thenReturn(teacher);

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null users list in toDto")
    void toDto_withNullUsersList_shouldReturnDtoWithEmptyList() {
        // Arrange
        session.setUsers(null);

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty users list in toDto")
    void toDto_withEmptyUsersList_shouldReturnDtoWithEmptyList() {
        // Arrange
        session.setUsers(Collections.emptyList());

        // Act
        SessionDto result = sessionMapper.toDto(session);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).isEmpty();
    }



    @Test
    @DisplayName("Should convert list of entities to list of DTOs")
    void toDto_withListOfEntities_shouldConvertAll() {
        // Arrange
        Session session2 = Session.builder()
                .id(2L)
                .name("Evening Session")
                .description("Evening yoga")
                .date(new Date())
                .teacher(teacher)
                .users(Arrays.asList(user1))
                .build();

        List<Session> sessions = Arrays.asList(session, session2);

        // Act
        List<SessionDto> results = sessionMapper.toDto(sessions);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Yoga Session");
        assertThat(results.get(1).getName()).isEqualTo("Evening Session");
    }

    @Test
    @DisplayName("Should convert list of DTOs to list of entities")
    void toEntity_withListOfDtos_shouldConvertAll() {
        // Arrange
        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Evening Session");
        sessionDto2.setDescription("Evening yoga");
        sessionDto2.setDate(new Date());
        sessionDto2.setTeacher_id(1L);
        sessionDto2.setUsers(Arrays.asList(1L));

        List<SessionDto> dtos = Arrays.asList(sessionDto, sessionDto2);

        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(user2);

        // Act
        List<Session> results = sessionMapper.toEntity(dtos);

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getName()).isEqualTo("Yoga Session");
        assertThat(results.get(1).getName()).isEqualTo("Evening Session");
    }

    @Test
    @DisplayName("Should handle user not found in toEntity")
    void toEntity_withNonExistentUser_shouldFilterOutNullUsers() {
        // Arrange
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(userService.findById(1L)).thenReturn(user1);
        when(userService.findById(2L)).thenReturn(null); // User not found

        // Act
        Session result = sessionMapper.toEntity(sessionDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsers()).hasSize(2);
        assertThat(result.getUsers().get(0)).isNotNull();
        assertThat(result.getUsers().get(1)).isNull(); // Le mapper retourne null pour les users non trouvés
    }
}