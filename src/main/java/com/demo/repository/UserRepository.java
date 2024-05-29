package com.demo.repository;

import com.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_EMPLOYEE'")
    List<User> getEmployees();

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_USER'")
    List<User> getUsers();
}
