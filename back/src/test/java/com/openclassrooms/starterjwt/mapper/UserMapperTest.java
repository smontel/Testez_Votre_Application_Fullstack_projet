package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // User Entity
        user = new User()
                .setId(1L)
                .setEmail("yoga@studio.com")
                .setLastName("Studio")
                .setFirstName("Yoga")
                .setPassword("hashedPassword123")
                .setAdmin(false)
                .setCreatedAt(now)
                .setUpdatedAt(now);

        // User DTO
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("yoga@studio.com");
        userDto.setLastName("Studio");
        userDto.setFirstName("Yoga");
        userDto.setPassword("hashedPassword123");
        userDto.setAdmin(false);
        userDto.setCreatedAt(now);
        userDto.setUpdatedAt(now);
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        // Act
        UserDto result = userMapper.toDto(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("yoga@studio.com");
        assertThat(result.getLastName()).isEqualTo("Studio");
        assertThat(result.getFirstName()).isEqualTo("Yoga");
        assertThat(result.getPassword()).isEqualTo("hashedPassword123");
        assertThat(result.isAdmin()).isFalse();
        assertThat(result.getCreatedAt()).isEqualTo(user.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    void toEntity_shouldConvertDtoToEntity() {
        // Act
        User result = userMapper.toEntity(userDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("yoga@studio.com");
        assertThat(result.getLastName()).isEqualTo("Studio");
        assertThat(result.getFirstName()).isEqualTo("Yoga");
        assertThat(result.getPassword()).isEqualTo("hashedPassword123");
        assertThat(result.isAdmin()).isFalse();
        assertThat(result.getCreatedAt()).isEqualTo(userDto.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(userDto.getUpdatedAt());
    }

    @Test
    void toDto_withAdminUser_shouldMapAdminFieldCorrectly() {
        // Arrange
        user.setAdmin(true);

        // Act
        UserDto result = userMapper.toDto(user);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isAdmin()).isTrue();
    }

    @Test
    void toDto_withNullEntity_shouldReturnNull() {
        // Act
        UserDto result = userMapper.toDto((User) null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toEntity_withNullDto_shouldReturnNull() {
        // Act
        User result = userMapper.toEntity((UserDto) null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toDto_withListOfEntities_shouldConvertAll() {
        // Arrange
        User user2 = new User()
                .setId(2L)
                .setEmail("admin@studio.com")
                .setLastName("Admin")
                .setFirstName("Super")
                .setPassword("adminPassword")
                .setAdmin(true)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now());

        List<User> users = Arrays.asList(user, user2);

        // Act
        List<UserDto> results = userMapper.toDto(users);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getEmail()).isEqualTo("yoga@studio.com");
        assertThat(results.get(0).isAdmin()).isFalse();
        assertThat(results.get(1).getId()).isEqualTo(2L);
        assertThat(results.get(1).getEmail()).isEqualTo("admin@studio.com");
        assertThat(results.get(1).isAdmin()).isTrue();
    }

    @Test
    void toEntity_withListOfDtos_shouldConvertAll() {
        // Arrange
        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("admin@studio.com");
        userDto2.setLastName("Admin");
        userDto2.setFirstName("Super");
        userDto2.setPassword("adminPassword");
        userDto2.setAdmin(true);
        userDto2.setCreatedAt(LocalDateTime.now());
        userDto2.setUpdatedAt(LocalDateTime.now());

        List<UserDto> userDtos = Arrays.asList(userDto, userDto2);

        // Act
        List<User> results = userMapper.toEntity(userDtos);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getEmail()).isEqualTo("yoga@studio.com");
        assertThat(results.get(0).isAdmin()).isFalse();
        assertThat(results.get(1).getId()).isEqualTo(2L);
        assertThat(results.get(1).getEmail()).isEqualTo("admin@studio.com");
        assertThat(results.get(1).isAdmin()).isTrue();
    }


    @Test
    void toDto_withEmptyList_shouldReturnEmptyList() {
        // Act
        List<UserDto> results = userMapper.toDto(Collections.emptyList());

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    void toDto_withNullList_shouldReturnNull() {
        // Act
        List<UserDto> results = userMapper.toDto((List<User>) null);

        // Assert
        assertThat(results).isNull();
    }



}