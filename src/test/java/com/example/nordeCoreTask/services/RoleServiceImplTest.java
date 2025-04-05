package com.example.nordeCoreTask.services;

import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role("ADMIN");
        adminRole.setId(UUID.randomUUID());

        userRole = new Role("USER");
        userRole.setId(UUID.randomUUID());
    }

    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Arrays.asList(adminRole, userRole));

        // Act
        List<Role> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void createRole_WithNewRole_ShouldSaveRole() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);

        // Act
        Role result = roleService.createRole(adminRole);

        // Assert
        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(roleRepository, times(1)).save(adminRole);
    }

    @Test
    void createRole_WithExistingRole_ShouldThrowException() {
        // Arrange
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> roleService.createRole(adminRole));
        verify(roleRepository, times(1)).findByName("ADMIN");
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateRole_WithExistingId_ShouldUpdateRole() {
        // Arrange
        UUID roleId = adminRole.getId();
        Role updatedRole = new Role("SUPER_ADMIN");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(adminRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        // Act
        Role result = roleService.updateRole(roleId, updatedRole);

        // Assert
        assertNotNull(result);
        assertEquals("SUPER_ADMIN", result.getName());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void updateRole_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> roleService.updateRole(roleId, adminRole));
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteRole_WithExistingId_ShouldDeleteRole() {
        // Arrange
        UUID roleId = adminRole.getId();
        when(roleRepository.existsById(roleId)).thenReturn(true);

        // Act
        roleService.deleteRole(roleId);

        // Assert
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void deleteRole_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        when(roleRepository.existsById(roleId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> roleService.deleteRole(roleId));
        verify(roleRepository, never()).deleteById(any());
    }
}
