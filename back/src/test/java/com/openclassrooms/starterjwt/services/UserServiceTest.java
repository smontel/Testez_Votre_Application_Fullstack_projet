package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void shouldReturnUserById(){
        User u = new User();
        u.setId(1L);
        u.setEmail("test@studio.com");
        u.setFirstName("Patrick");
        u.setLastName("Bertrand");
        u.setPassword("test1234!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));

        Optional<User> user = userRepository.findById(1L);

        assertThat(user).isPresent().contains(u);
    }

    @Test
    void shouldDeleteUser(){
        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }


}
