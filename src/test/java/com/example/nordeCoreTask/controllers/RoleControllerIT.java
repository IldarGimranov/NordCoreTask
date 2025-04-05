package com.example.nordeCoreTask.controllers;

import com.example.nordeCoreTask.models.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoleControllerIT extends BaseControllerTest {

    @Test
    void createRole_ShouldReturnCreatedRole() throws Exception {
        String roleJson = """
            {
                "name": "MODERATOR"
            }
            """;

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("MODERATOR"));
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoles() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ADMIN"))
                .andExpect(jsonPath("$[1].name").value("USER"));
    }

    @Test
    void updateRole_ShouldUpdateRoleName() throws Exception {
        // Arrange
        String updateJson = """
            {
                "name": "SUPER_ADMIN"
            }
            """;
        // Act & Assert
        mockMvc.perform(put("/api/roles/{id}", adminRole.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPER_ADMIN"));
    }

    @Test
    @Transactional
    void deleteRole_ShouldReturnNoContent() throws Exception {
        // Arrange
        Role tempRole = roleRepository.save(new Role("TEMPORARY"));

        // Act & Assert
        mockMvc.perform(delete("/api/roles/{id}", tempRole.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        assertFalse(roleRepository.existsById(tempRole.getId()));
    }
}
