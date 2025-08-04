package com.example.ecommerce_backend.Service;


import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // CREATE
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // READ - All
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // READ - By ID
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    // UPDATE
    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
            .map(user -> {
                user.setUsername(updatedUser.getUsername());
                user.setPassword(updatedUser.getPassword());
                user.setEmail(updatedUser.getEmail());
                user.setRoles(updatedUser.getRoles());
                return userRepository.save(user);
            }).orElse(null);
    }

    // DELETE
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
