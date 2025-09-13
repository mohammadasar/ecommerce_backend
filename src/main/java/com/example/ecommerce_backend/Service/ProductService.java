package com.example.ecommerce_backend.Service;

import org.springframework.stereotype.Service;

import com.example.ecommerce_backend.Modal.Product;
import com.example.ecommerce_backend.Repo.ProductRepository;import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> byTitle = productRepository.findByTitleContainingIgnoreCase(keyword);
        List<Product> byCategory = productRepository.findByCategoryContainingIgnoreCase(keyword);

        // Merge both results into one Set to avoid duplicates
        Set<Product> results = new HashSet<>();
        results.addAll(byTitle);
        results.addAll(byCategory);

        // Convert back to List
        return new ArrayList<>(results);
    }
}

