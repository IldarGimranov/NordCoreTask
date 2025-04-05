package com.example.nordeCoreTask.controllers;

import com.example.nordeCoreTask.DTO.UserDTO;
import com.example.nordeCoreTask.DTO.UserFilterRequest;
import com.example.nordeCoreTask.models.User;
import com.example.nordeCoreTask.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<User>> getUsersWithFilter(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = userService.getAllUsersFiltered(username, role, page, size);
        logger.debug("Найдено {} пользователей на странице {}", users.getContent().size(), page);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/filter")
    public ResponseEntity<Page<User>> filterUsers(@RequestBody UserFilterRequest filterRequest) {
        Page<User> users = userService.getAllUsersFiltered(
                filterRequest.getUsername(),
                filterRequest.getRole(),
                filterRequest.getPage(),
                filterRequest.getSize()
        );
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        logger.info("Получение пользователя с ID: {}", id);
        try {
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            logger.info("Пользователь с ID {} найден", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователя {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return getUsersWithFilter(null, null, page, size);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
        logger.info("Создание пользователя: {}", userDTO.getUsername());
        try {
            User user = new User(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getPassword()
            );
            User createdUser = userService.createUser(user, userDTO.getRoles());
            logger.info("Пользователь {} создан, ID: {}", userDTO.getUsername(), createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя {}: {}", userDTO.getUsername(), e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDTO userDTO) {
        logger.info("Обновление пользователя с ID: {}", id);
        try {
            User user = new User(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getPassword()
            );
            User updatedUser = userService.updateUser(id, user);
            logger.info("Пользователь с ID {} обновлен", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("Удаление пользователя с ID: {}", id);
        try {
            userService.deleteUser(id);
            logger.info("Пользователь с ID {} удален", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}

