package com.example.nordeCoreTask.services;

import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.models.User;
import com.example.nordeCoreTask.repositories.RoleRepository;
import com.example.nordeCoreTask.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role("ADMIN");
        adminRole.setId(UUID.randomUUID());

        userRole = new Role("USER");
        userRole.setId(UUID.randomUUID());

        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(UUID.randomUUID());
        testUser.setRoles(new HashSet<>(Arrays.asList(userRole)));
    }

    @Test
    void createUser_WithValidData_ShouldReturnUser() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.createUser(testUser, Set.of("USER"));

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(1, result.getRoles().size());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.createUser(testUser, Set.of("USER")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.createUser(testUser, Set.of("USER")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_WithNonExistingRole_ShouldThrowException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName("INVALID_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.createUser(testUser, Set.of("INVALID_ROLE")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        // Arrange
        UUID userId = testUser.getId();
        User updateData = new User("updateduser", "updated@example.com", "newpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            return savedUser;
        });

        // Act
        User result = userService.updateUser(userId, updateData);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertEquals("encodedNewPassword", result.getPassword());
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_WithEmptyPassword_ShouldNotUpdatePassword() {
        // Arrange
        UUID userId = testUser.getId();
        String oldEncodedPassword = testUser.getPassword();

        User updatedUser = new User("updateduser", "updated@example.com", "");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        assertNotEquals(oldEncodedPassword, result.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsersFiltered_ShouldReturnFilteredUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);

        when(userRepository.findByFilter(null, null, pageable)).thenReturn(userPage);

        // Act
        Page<User> result = userService.getAllUsersFiltered(null, null, 0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findByFilter(null, null, pageable);
    }

    @Test
    void deleteUser_WithExistingId_ShouldDeleteUser() {
        // Arrange
        UUID userId = testUser.getId();
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldThrowException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(any());
    }
}
