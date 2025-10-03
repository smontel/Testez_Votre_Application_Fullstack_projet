package com.openclassrooms.starterjwt.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;

@SpringBootTest
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    private Teacher teacher;
    private TeacherDto teacherDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // Teacher Entity
        teacher = Teacher.builder()
                .id(1L)
                .lastName("DELAHAYE")
                .firstName("Margot")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Teacher DTO
        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("DELAHAYE");
        teacherDto.setFirstName("Margot");
        teacherDto.setCreatedAt(now);
        teacherDto.setUpdatedAt(now);
    }

    @Test
    void toDto_shouldConvertEntityToDto() {
        // Act
        TeacherDto result = teacherMapper.toDto(teacher);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isEqualTo("DELAHAYE");
        assertThat(result.getFirstName()).isEqualTo("Margot");
        assertThat(result.getCreatedAt()).isEqualTo(teacher.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(teacher.getUpdatedAt());
    }

    @Test
    void toEntity_shouldConvertDtoToEntity() {
        // Act
        Teacher result = teacherMapper.toEntity(teacherDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isEqualTo("DELAHAYE");
        assertThat(result.getFirstName()).isEqualTo("Margot");
        assertThat(result.getCreatedAt()).isEqualTo(teacherDto.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(teacherDto.getUpdatedAt());
    }

    @Test
    void toDto_withNullEntity_shouldReturnNull() {
        // Act
        TeacherDto result = teacherMapper.toDto((Teacher) null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toEntity_withNullDto_shouldReturnNull() {
        // Act
        Teacher result = teacherMapper.toEntity((TeacherDto) null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toDto_withListOfEntities_shouldConvertAll() {
        // Arrange
        Teacher teacher2 = Teacher.builder()
                .id(2L)
                .lastName("THIERCELIN")
                .firstName("Hélène")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<Teacher> teachers = Arrays.asList(teacher, teacher2);

        // Act
        List<TeacherDto> results = teacherMapper.toDto(teachers);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getLastName()).isEqualTo("DELAHAYE");
        assertThat(results.get(1).getId()).isEqualTo(2L);
        assertThat(results.get(1).getLastName()).isEqualTo("THIERCELIN");
    }

    @Test
    void toEntity_withListOfDtos_shouldConvertAll() {
        // Arrange
        TeacherDto teacherDto2 = new TeacherDto();
        teacherDto2.setId(2L);
        teacherDto2.setLastName("THIERCELIN");
        teacherDto2.setFirstName("Hélène");
        teacherDto2.setCreatedAt(LocalDateTime.now());
        teacherDto2.setUpdatedAt(LocalDateTime.now());

        List<TeacherDto> teacherDtos = Arrays.asList(teacherDto, teacherDto2);

        // Act
        List<Teacher> results = teacherMapper.toEntity(teacherDtos);

        // Assert
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getId()).isEqualTo(1L);
        assertThat(results.get(0).getLastName()).isEqualTo("DELAHAYE");
        assertThat(results.get(1).getId()).isEqualTo(2L);
        assertThat(results.get(1).getLastName()).isEqualTo("THIERCELIN");
    }

    @Test
    void toDto_withNullFields_shouldHandleGracefully() {
        // Arrange
        Teacher teacherWithNulls = Teacher.builder()
                .id(1L)
                .lastName(null)
                .firstName(null)
                .createdAt(null)
                .updatedAt(null)
                .build();

        // Act
        TeacherDto result = teacherMapper.toDto(teacherWithNulls);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    void toEntity_withNullFields_shouldHandleGracefully() {
        // Arrange
        TeacherDto dtoWithNulls = new TeacherDto();
        dtoWithNulls.setId(1L);
        dtoWithNulls.setLastName(null);
        dtoWithNulls.setFirstName(null);
        dtoWithNulls.setCreatedAt(null);
        dtoWithNulls.setUpdatedAt(null);

        // Act
        Teacher result = teacherMapper.toEntity(dtoWithNulls);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isNull();
        assertThat(result.getFirstName()).isNull();
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    void bidirectionalMapping_shouldPreserveAllData() {
        // Act - Entity to DTO
        TeacherDto dto = teacherMapper.toDto(teacher);

        // Act - DTO back to Entity
        Teacher entity = teacherMapper.toEntity(dto);

        // Assert - All data preserved
        assertThat(entity.getId()).isEqualTo(teacher.getId());
        assertThat(entity.getLastName()).isEqualTo(teacher.getLastName());
        assertThat(entity.getFirstName()).isEqualTo(teacher.getFirstName());
        assertThat(entity.getCreatedAt()).isEqualTo(teacher.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(teacher.getUpdatedAt());
    }

}