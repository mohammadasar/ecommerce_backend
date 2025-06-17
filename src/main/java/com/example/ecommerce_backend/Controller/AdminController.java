package com.example.ecommerce_backend.Controller;


import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.example.ecommerce_backend.Modal.Product;
import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.ProductRepository;
import com.example.ecommerce_backend.Repo.UserRepository;

import java.io.IOException;
import org.springframework.util.StringUtils;


@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/admin")
public class AdminController {
	  @Autowired
	    private ProductRepository productRepository;

	  private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

	
	  @Autowired
	    private UserRepository userRepository;
	
	@Autowired
	 private PasswordEncoder encoder;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome Admin!";
    }
  

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("roles", authorities.stream().map(GrantedAuthority::getAuthority).toList());

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/upload")
    public Product uploadProduct(@RequestParam("image") MultipartFile image,
    	                       	 @RequestParam("category") String category,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 @RequestParam("price") double price,
                                 @RequestParam("quantity") int quantity) throws IOException {

        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(image.getOriginalFilename());

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs(); // Create the directory if it doesn't exist
        }

        File file = new File(uploadPath, filename);
        image.transferTo(file);

        Product product = new Product();
        product.setCategory(category);
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImageUrl("/uploads/" + filename);

        return productRepository.save(product);
    }

    // READ All Products
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // READ Single Product by ID
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE Product by ID
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                 @RequestParam(value = "image", required = false) MultipartFile image,
                                                 @RequestParam("category") String category,
                                                 @RequestParam("title") String title,
                                                 @RequestParam("description") String description,
                                                 @RequestParam("price") double price,
                                                 @RequestParam("quantity") int quantity) throws IOException {

        return productRepository.findById(id).map(product -> {
            product.setCategory(category);
            product.setTitle(title);
            product.setDescription(description);
            product.setPrice(price);
            product.setQuantity(quantity);

            if (image != null && !image.isEmpty()) {
                try {
                    String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
                    File uploadPath = new File(uploadDir);
                    if (!uploadPath.exists()) uploadPath.mkdirs();
                    File file = new File(uploadPath, filename);
                    image.transferTo(file);
                    product.setImageUrl("/uploads/" + filename);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed", e);
                }
            }

            return ResponseEntity.ok(productRepository.save(product));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE Product by ID
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
    
    
}
