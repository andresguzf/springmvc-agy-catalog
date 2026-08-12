package com.andres.course.agy.springboot.springmvc.app.services;

import com.andres.course.agy.springboot.springmvc.app.models.Role;
import com.andres.course.agy.springboot.springmvc.app.models.User;
import com.andres.course.agy.springboot.springmvc.app.repositories.RoleRepository;
import com.andres.course.agy.springboot.springmvc.app.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() != null) {
            Optional<User> existingOptional = userRepository.findById(user.getId());
            if (existingOptional.isPresent()) {
                User dbUser = existingOptional.get();
                if (user.getPassword() == null || user.getPassword().isBlank()) {
                    user.setPassword(dbUser.getPassword());
                } else if (!user.getPassword().equals(dbUser.getPassword()) && !user.getPassword().startsWith("$2a$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                }
            }
        } else {
            if (user.getPassword() != null && !user.getPassword().isBlank() && !user.getPassword().startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        // Attach persisted roles from DB to avoid transient instances
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Role> persistedRoles = new ArrayList<>();
            for (Role r : user.getRoles()) {
                if (r.getId() != null) {
                    roleRepository.findById(r.getId()).ifPresent(persistedRoles::add);
                } else if (r.getName() != null) {
                    roleRepository.findByName(r.getName()).ifPresent(persistedRoles::add);
                }
            }
            user.setRoles(persistedRoles);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerUser(User user) {
        user.setEnabled(true);
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        Optional<Role> roleUserOptional = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        roleUserOptional.ifPresent(roles::add);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(!Boolean.TRUE.equals(user.getEnabled()));
            userRepository.save(user);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveAdmins() {
        return userRepository.countByRolesNameAndEnabledTrue("ROLE_ADMIN");
    }
}
