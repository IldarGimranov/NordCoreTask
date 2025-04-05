package com.example.nordeCoreTask.controllers;

import com.example.nordeCoreTask.DTO.RoleDTO;
import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.services.RoleService;
import com.example.nordeCoreTask.services.RoleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        logger.debug("Получение списка всех ролей");
        List<Role> roles = roleService.getAllRoles();
        logger.info("Возвращено {} ролей", roles.size());
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO roleDTO) {
        logger.info("Создание новой роли: {}", roleDTO.getName());
        try {
            Role role = new Role(roleDTO.getName());
            Role createdRole = roleService.createRole(role);
            logger.info("Роль {} успешно создана с ID: {}", roleDTO.getName(), createdRole.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (Exception e) {
            logger.error("Ошибка при создании роли {}: {}", roleDTO.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable UUID id, @RequestBody RoleDTO roleDTO) {
        logger.info("Обновление роли с ID: {}, новые данные: {}", id, roleDTO.getName());
        try {
            Role role = new Role(roleDTO.getName());
            Role updatedRole = roleService.updateRole(id, role);
            logger.info("Роль с ID {} успешно обновлена", id);
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении роли {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        logger.info("Удаление роли с ID: {}", id);
        try {
            roleService.deleteRole(id);
            logger.info("Роль с ID {} успешно удалена", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Ошибка при удалении роли {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
