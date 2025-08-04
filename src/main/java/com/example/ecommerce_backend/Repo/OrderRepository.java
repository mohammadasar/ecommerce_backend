package com.example.ecommerce_backend.Repo;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.ecommerce_backend.Modal.order;

public interface OrderRepository extends MongoRepository<order, String> {
}
