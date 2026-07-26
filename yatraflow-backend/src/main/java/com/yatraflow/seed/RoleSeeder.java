package com.yatraflow.seed;

import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.repository.RoleRepository;
import com.yatraflow.role.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {


    private final RoleService roleService;

    @Override
    public void run(String... args) {

        log.info("Starting role seeding...");

        roleService.seedDefaultRoles();

        log.info("Role seeding completed.");

    }

}
