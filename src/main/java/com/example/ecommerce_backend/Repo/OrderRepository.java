package com.example.ecommerce_backend.Repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce_backend.Modal.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
	 List<Order> findByUserId(String userId);
}
