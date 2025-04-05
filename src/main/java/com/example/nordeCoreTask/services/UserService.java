package com.example.nordeCoreTask.services;

import com.example.nordeCoreTask.models.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {

    User createUser(User user, Set<String> roleNames);

    Optional<User> getUserById(UUID id);

    Page<User> getAllUsersFiltered(String username, String role, int page, int size);

    User updateUser(UUID id, User userDetails);

    void deleteUser(UUID id);

}
