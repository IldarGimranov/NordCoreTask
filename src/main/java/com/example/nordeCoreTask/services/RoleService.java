package com.example.nordeCoreTask.services;

import com.example.nordeCoreTask.models.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<Role> getAllRoles();

    Role createRole(Role role);

    Role updateRole(UUID id, Role roleDetails);

    void deleteRole(UUID id);

}
