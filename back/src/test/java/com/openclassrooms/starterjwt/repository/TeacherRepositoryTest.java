package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TeacherRepositoryTest {
    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher firstTeacher;
    private Teacher secondTeacher;

    @BeforeEach
    void setUp() {
        teacherRepository.deleteAll();

        firstTeacher = new Teacher();
        firstTeacher.setFirstName("Margot");
        firstTeacher.setLastName("DELAHAYE");
        teacherRepository.save(firstTeacher);

        secondTeacher = new Teacher();
        secondTeacher.setFirstName("Helene");
        secondTeacher.setLastName("THIERCELIN");
        teacherRepository.save(secondTeacher);
    }

    @Test
    void shouldGetAllTeacher(){
        List<Teacher> teachers = teacherRepository.findAll();

        assertEquals(2, teachers.size());
        assertEquals("Margot", teachers.get(0).getFirstName());
    }

    @Test
    void shouldGetTeacherById() {
        Optional<Teacher> teacherOptional = teacherRepository.findById(firstTeacher.getId());

        assertTrue(teacherOptional.isPresent());
        Teacher teacher = teacherOptional.get();
        assertEquals("Margot", teacher.getFirstName());
        assertEquals("DELAHAYE", teacher.getLastName());
    }

    @Test
    void shouldSaveTeacher(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("testFName");
        teacher.setLastName("testLName");

        Teacher savedTeacher = teacherRepository.save(teacher);

        assertNotNull(savedTeacher.getId());
        assertEquals("testFName", savedTeacher.getFirstName());
    }

    @Test
    void shouldUpdateTeacher() {
        Optional<Teacher> teacherOptional = teacherRepository.findById(secondTeacher.getId());
        assertTrue(teacherOptional.isPresent());

        Teacher teacher = teacherOptional.get();
        teacher.setFirstName("updatedName");

        Teacher updatedTeacher = teacherRepository.save(teacher);

        assertEquals("updatedName", updatedTeacher.getFirstName());
    }

    @Test
    void shouldDeleteTeacher(){
        teacherRepository.deleteById(secondTeacher.getId());
        Optional<Teacher> session = teacherRepository.findById(2L);
        assertFalse(session.isPresent());
    }
}
