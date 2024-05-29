package com.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users=new HashSet<>();

    public Role(String name){
        this.name=name;
    }

    public void assignRoleToUser(User user){
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void removeUserFromRole(User user){
        user.getRoles().remove(this);
        this.getUsers().remove(user);

    }

    public void removeAllUsersFromRole(){
        if (this.getUsers() != null){
            List<User> roleUsers = this.getUsers().stream().toList();
            roleUsers.forEach(this :: removeUserFromRole);
        }
    }
    public  String getName(){
        return name != null? name : "";
    }
}
