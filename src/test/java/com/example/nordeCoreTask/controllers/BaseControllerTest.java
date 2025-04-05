package com.example.nordeCoreTask.controllers;

import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.models.User;
import com.example.nordeCoreTask.repositories.RoleRepository;
import com.example.nordeCoreTask.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    protected Role adminRole;
    protected Role userRole;
    protected User testUser;

    @BeforeEach
    void setUp() {
        // Очистка базы перед каждым тестом
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Создание тестовых ролей
        adminRole = roleRepository.save(new Role("ADMIN"));
        userRole = roleRepository.save(new Role("USER"));

        // Создание тестового пользователя
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setRoles(Set.of(userRole));
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
