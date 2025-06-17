package com.example.ecommerce_backend.Repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce_backend.Modal.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
