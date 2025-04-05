package com.example.nordeCoreTask.controllers;

import com.example.nordeCoreTask.DTO.AuthRequest;
import com.example.nordeCoreTask.services.AuthService;
import com.example.nordeCoreTask.services.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API для аутентификации")
public class AuthController {

    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в систему", description = "Полученный JWT токен")
    public ResponseEntity<String> login(@RequestBody AuthRequest authRequest) {
        logger.info("Запрос на аутентификацию для пользователя: {}", authRequest.getUsername());
        try {
            String token = authService.authenticate(authRequest.getUsername(), authRequest.getPassword());
            logger.info("Успешная аутентификация пользователя: {}", authRequest.getUsername());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            logger.error("Ошибка аутентификации для пользователя {}: {}", authRequest.getUsername(), e.getMessage());
            throw e;
        }
    }
}
