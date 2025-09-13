package com.example.ecommerce_backend.Repo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce_backend.Modal.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
	  // Search by product name or description (case-insensitive)
	 List<Product> findByTitleContainingIgnoreCase(String keyword);
	    List<Product> findByCategoryContainingIgnoreCase(String keyword);}
