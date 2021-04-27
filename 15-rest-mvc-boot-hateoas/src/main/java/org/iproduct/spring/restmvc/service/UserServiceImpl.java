package org.iproduct.spring.restmvc.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.iproduct.spring.restmvc.dao.UserRepository;
import org.iproduct.spring.restmvc.exception.EntityNotFoundException;
import org.iproduct.spring.restmvc.model.Role;
import org.iproduct.spring.restmvc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.iproduct.spring.restmvc.model.Role.ROLE_USER;

@Service
@Primary
@Slf4j
@Validated
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repo;

    @Autowired
    RoleService roles;

    @Override
    @PostFilter("filterObject.id == authentication.principal.id or hasAuthority('ALL_USER_READ')")
    public List<User> getUsers() {
        return repo.findAll();
    }

    @Override
    public User createUser(@Valid User user) {
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Lists.asList(roles.getRoleByName(ROLE_USER).get(), new Role[]{}));
        } else {
            List<Role> expandedRoles = user.getRoles().stream()
                    .map(role -> roles.getRoleByName(role.getName()))
                    .filter(roleOpt -> roleOpt.isPresent())
                    .map(roleOpt -> roleOpt.get())
                    .collect(Collectors.toList());
            log.info(">>> Expanded roles: {}", expandedRoles);
            user.setRoles(expandedRoles);
        }
        log.info(">>> User Password: {}", user.getPassword());
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        return repo.save(user);
    }

    @Override
    public User createUserIfNotExist(User user) {
        Optional<User> result = repo.findByUsername(user.getUsername());
        if(result.isPresent()) {
            return result.get();
        } else {
            log.info("Creating default User: {}", user);
            return createUser(user);
        }
    }

    @Override
    public User updateUser(User user) {
        user.setUpdated(LocalDateTime.now());
        return repo.save(user);
    }

    @Override
    public User getUserById(String id) {
        if(id == null) return null;
        return repo.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User with ID=%s not found.", id)));
    }

    @Override
    public User getUserByUsername(String name) {
        return repo.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + name + " not found."));
    }

    @Override
    @RolesAllowed("ADMIN")
    @Secured("ROLE_ADMIN")
    public User deleteUser(String id) {
        User old = repo.findById(id).orElseThrow( () ->
                new EntityNotFoundException(String.format("User with ID=%s not found.", id)));
        repo.deleteById(id);
        return old;
    }
}
