package com.yatraflow.security.admin;

import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.service.RoleService;
import com.yatraflow.user.entity.User;
import com.yatraflow.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {



    private final AdminProperties adminProperties;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override

    public void run(String... args) {

        log.info("Starting admin user seeding...");

        String adminEmail = adminProperties.getEmail();

        // ---------------------------------------------------------
        // Check whether admin already exists
        // ---------------------------------------------------------

        if (userService.existsByEmail(adminEmail)) {
            log.info("Admin user already exists. Skipping admin seeding.");
            return;
        }

        // ---------------------------------------------------------
        // Get ADMIN role
        // ---------------------------------------------------------

        Role adminRole = roleService.getRoleByName(RoleName.ROLE_ADMIN);

        // ---------------------------------------------------------
        // Create Admin User
        // ---------------------------------------------------------



        User admin = User.builder()
                .firstName("System")
                .lastName("Admin")
                .email(adminEmail)
                .password(
                        passwordEncoder.encode(
                                adminProperties.getPassword()
                        )
                )

                .phoneNumber(adminProperties.getPhoneNumber())

                .enabled(true)
                .emailVerified(true)
                .accountLocked(false)
                .build();

        // ---------------------------------------------------------
        // Assign ROLE_ADMIN
        // ---------------------------------------------------------

        admin.getRoles().add(adminRole);

        // ---------------------------------------------------------
        // Save Admin
        // ---------------------------------------------------------

        User savedAdmin = userService.createUser(admin);

        log.info(
                "Admin user created successfully. Admin ID: {}",
                savedAdmin.getId()
        );
    }
}
