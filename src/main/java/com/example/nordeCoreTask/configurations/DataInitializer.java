package com.example.nordeCoreTask.configurations;

import com.example.nordeCoreTask.models.Role;
import com.example.nordeCoreTask.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);

            Role userRole = new Role("USER");
            roleRepository.save(userRole);
        }
    }
}
