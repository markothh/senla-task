package model.service;

import model.entity.User;
import model.enums.UserRole;
import model.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1, "testUser", "password", UserRole.USER);
    }

    @Test
    void getUserById_found_returnsUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(1, result.getId());
        assertEquals("testUser", result.getName());
    }

    @Test
    void getUserById_notFound_throwsNoSuchElement() {
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserById(99));
    }

    @Test
    void getUserByName_found_returnsUser() {
        when(userRepository.findByName("testUser")).thenReturn(Optional.of(user));

        User result = userService.getUserByName("testUser");

        assertEquals("testUser", result.getName());
    }

    @Test
    void getUserByName_notFound_throwsNoSuchElement() {
        when(userRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> userService.getUserByName("unknown"));
    }

    @Test
    void getUsers_returnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getUsers();

        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0).getName());
    }

    @Test
    void getUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<User> result = userService.getUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void createUser_returnsCreatedUser() {
        User result = userService.createUser("newUser", "hashedPassword");

        assertNotNull(result);
        assertEquals("newUser", result.getName());
        verify(userRepository).save(any(User.class));
    }
}
