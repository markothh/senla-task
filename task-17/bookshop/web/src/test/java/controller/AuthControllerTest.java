package controller;

import jakarta.servlet.ServletException;
import model.entity.DTO.UserRegisterDTO;
import model.entity.User;
import model.enums.UserRole;
import model.service.UserService;
import model.utils.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    private AuthController authController;
    private User user;

    @BeforeEach
    void setUp() {
        String hashedPassword = new BCryptPasswordEncoder().encode("password");
        user = new User(1, "testUser", hashedPassword, UserRole.USER);
        authController = new AuthController(userService, jwtUtil);
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        when(userService.getUserByName("testUser")).thenReturn(user);
        when(jwtUtil.generateToken(anyInt(), anyString(), anyString())).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_userNotFound_throwsNoSuchElement() throws Exception {
        when(userService.getUserByName("unknown"))
                .thenThrow(new NoSuchElementException("Пользователь не найден"));

        try {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"username\":\"unknown\",\"password\":\"123123\"}"));
            org.junit.jupiter.api.Assertions.fail("Expected ServletException");
        } catch (ServletException e) {
            assertInstanceOf(NoSuchElementException.class, e.getCause());
        }
        verify(userService).getUserByName("unknown");
    }

    @Test
    void login_wrongPassword_returnsUnauthorized() throws Exception {
        User userWithWrongHash = new User("testUser", "123123");
        when(userService.getUserByName("testUser")).thenReturn(userWithWrongHash);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"456456\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void register_newUser_returnsSuccess() {
        UserRegisterDTO registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("newUser");
        registerDTO.setPassword("password");

        when(userService.getUserByName("newUser"))
                .thenThrow(new NoSuchElementException("Пользователь не найден"));
        when(userService.createUser(anyString(), anyString())).thenReturn(user);

        ResponseEntity<?> result = authController.register(registerDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertInstanceOf(model.entity.DTO.MessageDTO.class, result.getBody());
    }

    @Test
    void register_existingUser_returnsConflict() throws Exception {
        when(userService.getUserByName("testUser")).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testUser\",\"password\":\"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }
}
