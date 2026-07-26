package com.yatraflow.role.service.impl;

import com.yatraflow.exception.ResourceNotFoundException;
import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;
import com.yatraflow.role.repository.RoleRepository;
import com.yatraflow.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private  final RoleRepository roleRepository;

    @Override
    public void seedDefaultRoles() {

        log.info("Seeding default roles....");

        createRoleIfNotExists(RoleName.ROLE_USER);
        createRoleIfNotExists(RoleName.ROLE_ADMIN);

        log.info("Default roles seeded successfully.");
    }

    @Override
    public Role getRoleByName(RoleName roleName) {

        validateRoleName(roleName);

        log.info("Fetching role : {}", roleName);
        return roleRepository.findByName(roleName).orElseThrow(
                () -> {
                    log.error("Role not found : {}", roleName);

                    return new ResourceNotFoundException("Role not found : " + roleName);
                }
        );
    }

    @Override
    public boolean exists(RoleName roleName) {
        validateRoleName(roleName);

        return roleRepository.existsByName(roleName);

    }

    @Override
    public Set<Role> getDefaultRoles() {
        return Set.of(getRoleByName(RoleName.ROLE_USER));
    }

    @Override
    public Set<Role> getRolesByNames(Set<RoleName> roleNames) {
        return Set.of(
                getRoleByName(RoleName.ROLE_USER)
        );
    }

    private void createRoleIfNotExists(RoleName roleName){

        validateRoleName(roleName);

        if(exists(roleName)) {
            log.info(" {} already Exists. Skipping ...", roleName);
            return;
        }

        Role role = buildrole(roleName);

        roleRepository.save(role);

        log.info(" {} created successfully.", roleName);
    }

    private Role buildrole(RoleName roleName){

        Role role = new Role();
        role.setName(roleName);

        return role;

    }

    private void validateRoleName(RoleName roleName){

        if(Objects.isNull(roleName)) {
            log.error("Role name cannot be null.");

            throw new IllegalArgumentException("Role name cannot be null");
        }
    }
}
