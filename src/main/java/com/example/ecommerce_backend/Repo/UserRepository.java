package com.example.ecommerce_backend.Repo;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce_backend.Modal.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

