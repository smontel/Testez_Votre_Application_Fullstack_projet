package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class TeacherServiceTest {

    @Mock
    TeacherRepository teacherRepository;

    @InjectMocks
    TeacherService teacherService;

    @Test
    void shouldReturnAllTeachers(){
        Teacher t1 = new Teacher();
        t1.setLastName("GRANGIER");
        t1.setFirstName("Beatrice");

        Teacher t2 = new Teacher();
        t2.setLastName("ROBERT");
        t1.setFirstName("Bernard");

        when(teacherRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Teacher> teachers = teacherRepository.findAll();


        assertThat(teachers).hasSize(2).containsExactly(t1, t2);
    }

    @Test
    void shouldReturnById(){
        Teacher t = new Teacher();
        t.setId(1L);
        t.setLastName("GRANGIER");
        t.setFirstName("Beatrice");

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(t));

        Optional<Teacher> teacher = teacherRepository.findById(1L);

        assertThat(teacher).isPresent().contains(t);
    }
}
