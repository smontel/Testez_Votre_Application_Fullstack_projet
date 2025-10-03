package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class SessionServiceTest {

    @Mock
    SessionRepository sessionRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SessionService sessionService;

    @Test
    void shouldReturnSessionOnCreate(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");

        Session session = new Session();
        session.setId(1L);
        session.setName("test session");
        session.setDescription("test description session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(u1, u2));

        when(sessionRepository.save(session)).thenReturn(session);

        assertThat(sessionService.create(session)).isEqualTo(session);
    }

    @Test
    void shouldDeleteSession(){
        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void shouldReturnArrayOfSession(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");

        Session s1 = new Session();
        s1.setId(1L);
        s1.setName("test session");
        s1.setDescription("test description session");
        s1.setDate(new Date());
        s1.setTeacher(teacher);
        s1.setUsers(Arrays.asList(u1, u2));

        Session s2 = new Session();
        s2.setId(2L);
        s2.setName("test session2");
        s2.setDescription("test description session2");
        s2.setDate(new Date());
        s2.setTeacher(teacher);
        s2.setUsers(Arrays.asList(u1, u2));

        when(sessionRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        assertThat(sessionService.findAll()).hasSize(2).containsExactlyInAnyOrder(s1, s2);
    }

    @Test
    void shouldReturnSessionById(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");

        Session session = new Session();
        session.setId(1L);
        session.setName("test session");
        session.setDescription("test description session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(u1, u2));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session sessionFetched = sessionService.getById(1L);
        assertThat(sessionFetched).isEqualTo(session);
    }

    @Test
    void shouldUpdateSession(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");


        Session session = new Session();
        session.setId(1L);
        session.setName("test session");
        session.setDescription("test description session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(Arrays.asList(u1, u2));

        when(sessionRepository.save(session)).thenReturn(session);

        assertThat(sessionService.update(1L, session)).isEqualTo(session);
    }

    @Test
    void shouldAddSessionParticipation(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");

        User u3 = new User();
        u3.setId(3L);
        u3.setEmail("test3@studio.com");
        u3.setFirstName("Jean");
        u3.setLastName("Valjean");
        u3.setPassword("test1234!");

        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);

        List<User> userListUpdated = new ArrayList<>();
        userListUpdated.add(u1);
        userListUpdated.add(u2);
        userListUpdated.add(u3);

        Session session = new Session();
        session.setId(1L);
        session.setName("test session");
        session.setDescription("test description session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(userList);

        Session sessionUpdated = new Session();
        sessionUpdated.setId(1L);
        sessionUpdated.setName("test session");
        sessionUpdated.setDescription("test description session");
        sessionUpdated.setDate(new Date());
        sessionUpdated.setTeacher(teacher);
        sessionUpdated.setUsers(userListUpdated);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(3L)).thenReturn(Optional.of(u3));
        when(sessionRepository.save(sessionUpdated)).thenReturn(sessionUpdated);

        sessionService.participate(1L, 3L);

        verify(sessionRepository).save(sessionUpdated);
    }

    @Test
    void shouldRemoveUserFromParticipate(){
        Teacher teacher = new Teacher();
        teacher.setFirstName("George");
        teacher.setLastName("BERNARD");

        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("test@studio.com");
        u1.setFirstName("Patrick");
        u1.setLastName("BERTRAND");
        u1.setPassword("test1234!");

        User u2 = new User();
        u2.setId(2L);
        u2.setEmail("test2@studio.com");
        u2.setFirstName("Patrick");
        u2.setLastName("BERTRAND");
        u2.setPassword("test1234!");

        User u3 = new User();
        u3.setId(3L);
        u3.setEmail("test3@studio.com");
        u3.setFirstName("Jean");
        u3.setLastName("Valjean");
        u3.setPassword("test1234!");

        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);
        userList.add(u3);

        List<User> userListUpdated = new ArrayList<>();
        userListUpdated.add(u1);
        userListUpdated.add(u2);

        Session session = new Session();
        session.setId(1L);
        session.setName("test session");
        session.setDescription("test description session");
        session.setDate(new Date());
        session.setTeacher(teacher);
        session.setUsers(userList);

        Session sessionUpdated = new Session();
        sessionUpdated.setId(1L);
        sessionUpdated.setName("test session");
        sessionUpdated.setDescription("test description session");
        sessionUpdated.setDate(new Date());
        sessionUpdated.setTeacher(teacher);
        sessionUpdated.setUsers(userListUpdated);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(3L)).thenReturn(Optional.of(u3));
        when(sessionRepository.save(sessionUpdated)).thenReturn(sessionUpdated);

        sessionService.noLongerParticipate(1L, 3L);

        verify(sessionRepository).save(sessionUpdated);
    }


}
