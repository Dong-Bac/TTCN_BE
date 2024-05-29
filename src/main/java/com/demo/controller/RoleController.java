package com.demo.controller;

import com.demo.model.Role;
import com.demo.model.User;
import com.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/roles")
@CrossOrigin(origins = "http://localhost:5500/")
@RequiredArgsConstructor
public class RoleController {
    @Autowired
    public RoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role theRole){
        roleService.createRole(theRole);
        return ResponseEntity.ok("New role created successfully");
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId
    ){
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-user-to-role")
    public User assignUserToRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId){
        return roleService.assignRoleToUser(userId, roleId);
    }
}
