package controller;

import exception.ErrorResponse;
import model.entity.DTO.MessageDTO;
import model.entity.DTO.UserAuthDTO;
import model.entity.DTO.UserRegisterDTO;
import model.entity.User;
import model.service.UserService;
import model.utils.jwt.JwtUtil;
import model.utils.jwt.JwtDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger();
    private String INVALID_USERNAME_OR_PASSWORD_ERROR_MSG = "Неверное имя пользователя или пароль";
    private String USERNAME_CONFLICT_ERROR_MSG = "Пользователь с таким именем уже существует";
    private static final String USER_CREATED_SUCCESS_MSG = "Новый пользователь с именем '%s' успешно создан";

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAuthDTO request) {
        User user = userService.getUserByName(request.getUsername());

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.debug(INVALID_USERNAME_OR_PASSWORD_ERROR_MSG);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(
                            "UNAUTHORIZED",
                            INVALID_USERNAME_OR_PASSWORD_ERROR_MSG
                    ));
        }

        String token = jwtUtil.generateToken(user.getId(), user.getName(), user.getRole().toString());

        return ResponseEntity.ok(new JwtDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO request) {
        try {
            userService.getUserByName(request.getUsername());
        } catch (NoSuchElementException e) {
            String hashedPassword = passwordEncoder.encode(request.getPassword());
            User user = userService.createUser(request.getUsername(), hashedPassword);
            return ResponseEntity.ok(new MessageDTO("OK", String.format(USER_CREATED_SUCCESS_MSG, user.getName())));
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        "CONFLICT",
                        USERNAME_CONFLICT_ERROR_MSG
                ));
    }
}