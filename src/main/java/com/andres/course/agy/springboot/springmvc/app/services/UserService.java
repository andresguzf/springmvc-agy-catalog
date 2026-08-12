package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Role;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User save(User user);

    User registerUser(User user);

    void deleteById(Long id);

    List<Role> findAllRoles();

    Optional<Role> findRoleByName(String name);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void toggleUserStatus(Long id);

    long countActiveAdmins();
}
