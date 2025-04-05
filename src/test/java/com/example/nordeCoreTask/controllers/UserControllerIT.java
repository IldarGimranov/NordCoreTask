package com.example.nordeCoreTask.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.util.Set;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerIT extends BaseControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        String userJson = """
            {
                "username": "newuser",
                "email": "new@example.com",
                "password": "newpass123",
                "roles": ["USER"]
            }
            """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.roles[0].name").value("USER"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_ShouldUpdateUserData() throws Exception {
        String updateJson = """
            {
                "username": "updateduser",
                "email": "updated@example.com",
                "password": "newpassword",
                "roles": ["ADMIN"]
            }
            """;

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.roles[0].name").value("USER"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
        mockMvc.perform(get("/api/users?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}