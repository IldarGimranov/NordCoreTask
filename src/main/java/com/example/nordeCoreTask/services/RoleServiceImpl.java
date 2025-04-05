package com.example.nordeCoreTask.services;

import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.repositories.RoleRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new RuntimeException("Роль уже существует");
        }
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(UUID id, Role roleDetails) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        role.setName(roleDetails.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Роль не найдена");
        }
        roleRepository.deleteById(id);
    }
}
