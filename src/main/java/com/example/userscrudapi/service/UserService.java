package com.example.userscrudapi.service;

import com.example.userscrudapi.model.User;
import com.example.userscrudapi.repo.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllActiveUsers() {
        return userRepository.findByDeletedFalse();
    }

    public List<User> findAllDeletedUsers() {
        return userRepository.findByDeletedTrue();
    }

    // admin only
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id){
        return userRepository.findUserById(id);
    }

    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new RuntimeException("User by username '" + username + "' was not found."));
    }

    public User findUserByEmail(String email){
        return userRepository.findUserByEmail(email)
                .filter(user -> !user.isDeleted())
                .orElseThrow(() -> new RuntimeException("User by email '" + email + "' was not found."));
    }

    // combine update and add methods?
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("Could not delete! User by id '" + id + "' was not found."));
        user.setDeleted(true);
        userRepository.save(user);
    }

    public void deleteAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(u -> {
            u.setDeleted(true);
        });
        userRepository.saveAll(users);

    }
}
