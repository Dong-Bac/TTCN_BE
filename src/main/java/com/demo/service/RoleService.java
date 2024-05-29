package com.demo.service;

import com.demo.model.Role;
import com.demo.model.User;

import java.util.List;

public interface RoleService {
    List<Role> getRoles();
    Role createRole(Role role);
    void deleteRole(Long id);
    Role findByName(String name);

    User removeUserFromRole(Long userId, Long roleId);
    User assignRoleToUser(Long userId, Long roleId);
    Role romoveAllUsersFromRole(Long roleId);
}
