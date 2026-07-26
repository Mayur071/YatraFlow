package com.yatraflow.role.service;

import com.yatraflow.role.entity.Role;
import com.yatraflow.role.entity.RoleName;

import java.util.Set;

public interface RoleService {

    // seeder
    void seedDefaultRoles();

    // Registration
    Role getRoleByName(RoleName roleName);

    // Admin
//    Role createRole(RoleName roleName);
//
//    Role updateRole(Long roleId, RoleName roleName);
//
//    void deleteRole(Long roleId);

    // Query
    boolean exists(RoleName roleName);

    Set<Role> getDefaultRoles();

    Set<Role> getRolesByNames(Set<RoleName> roleNames);



}
