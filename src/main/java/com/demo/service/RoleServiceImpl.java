package com.demo.service;

import com.demo.exception.RoleAlreadyExistException;
import com.demo.model.Role;
import com.demo.model.User;
import com.demo.repository.RoleRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        String roleName="ROLE_"+role.getName().toUpperCase();
        Role theRole=new Role(roleName);
        if(roleRepository.existsByName(roleName)){
            throw  new RoleAlreadyExistException(role.getName()+"role already exists");
        }
        return roleRepository.save(theRole);
    }

    @Override
    public void deleteRole(Long id) {
        this.roleRepository.deleteById(id);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(role.isPresent()&&role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Optional<User> user=userRepository.findById(userId);
        Optional<Role> role=roleRepository.findById(roleId);
        if(user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UsernameNotFoundException(user.get().getUsername()+" is alreadly assigned to the" + role.get().getName()+" role");
        }

        if (role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    @Override
    public Role romoveAllUsersFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUsersFromRole);
        return roleRepository.save(role.get());
    }
}
