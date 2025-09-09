package com.example.ecommerce_backend.Controller;


import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ecommerce_backend.Config.CloudinaryConfig;
import com.example.ecommerce_backend.Modal.Product;
import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.ProductRepository;
import com.example.ecommerce_backend.Repo.UserRepository;
import com.example.ecommerce_backend.Service.UserService;

import java.io.IOException;
import java.security.Principal;

import org.springframework.util.StringUtils;


//@CrossOrigin(origins = "http://127.0.0.1:5500")

@CrossOrigin(origins = "https://devwerxoil.netlify.app")

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
    private UserService userService;
	
	  @Autowired
	    private ProductRepository productRepository;

	  private final String uploadDir = System.getProperty("user.dir") + "/uploads/";
        
	  
	  @Autowired
	  private Cloudinary cloudinary;


	
	  @Autowired
	    private UserRepository userRepository;
	
	@Autowired
	 private PasswordEncoder encoder;

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "Welcome Admin!";
    }
  

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
    
//  get user  address details
    
    // Update address for logged-in user
    @PutMapping("/update-address")
    public ResponseEntity<?> updateAddress(@RequestBody User updatedInfo, Principal principal) {
        String username = principal.getName(); // from JWT
        return userRepository.findByUsername(username).map(user -> {
            user.setFullName(updatedInfo.getFullName());
            user.setPhone(updatedInfo.getPhone());
            user.setAltPhone(updatedInfo.getAltPhone());
            user.setPincode(updatedInfo.getPincode());
            user.setAddress(updatedInfo.getAddress());
            user.setState(updatedInfo.getState());
            user.setDistrict(updatedInfo.getDistrict());
            userRepository.save(user);
            return ResponseEntity.ok("Address updated successfully");
        }).orElse(ResponseEntity.notFound().build());
    }


    // Get saved address for logged-in user
    @GetMapping("/get-address/{userId}")
    public ResponseEntity<?> getAddress(@PathVariable String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Map<String, String> addressData = new HashMap<>();
                    addressData.put("fullName", user.getFullName());
                    addressData.put("phone", user.getPhone());
                    addressData.put("address", user.getAddress());
                    return ResponseEntity.ok(addressData);
                })
                .orElse(ResponseEntity.notFound().build());
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
    
    
//    this is for local database
//    @PostMapping("/upload")
//    public Product uploadProduct(@RequestParam("image") MultipartFile image,
//    	                       	 @RequestParam("category") String category,
//                                 @RequestParam("title") String title,
//                                 @RequestParam("description") String description,
//                                 @RequestParam("price") double price,
//                                 @RequestParam("quantity") int quantity) throws IOException {
//
//        String filename = UUID.randomUUID() + "_" + StringUtils.cleanPath(image.getOriginalFilename());
//
//        File uploadPath = new File(uploadDir);
//        if (!uploadPath.exists()) {
//            uploadPath.mkdirs(); // Create the directory if it doesn't exist
//        }
//
//        File file = new File(uploadPath, filename);
//        image.transferTo(file);
//
//        Product product = new Product();
//        product.setCategory(category);
//        product.setTitle(title);
//        product.setDescription(description);
//        product.setPrice(price);
//        product.setQuantity(quantity);
//        product.setImageUrl("/uploads/" + filename);
//
//        return productRepository.save(product);
//    }
    
    
//    this is for cloudinary
    @PostMapping("/upload")
    public Product uploadProduct(@RequestParam("image") MultipartFile image,
                                 @RequestParam("category") String category,
                                 @RequestParam("title") String title,
                                 @RequestParam("description") String description,
                                 @RequestParam("price") double price,
                                 @RequestParam("quantity") int quantity) throws IOException {

        // ✅ Upload image to Cloudinary
        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = uploadResult.get("secure_url").toString();

        // ✅ Create and save product with Cloudinary image URL
        Product product = new Product();
        product.setCategory(category);
        product.setTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setImageUrl(imageUrl);  // ✅ Use Cloudinary URL here

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
                    // ✅ Upload image to Cloudinary
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());

                    // ✅ Get secure URL from Cloudinary response
                    String imageUrl = uploadResult.get("secure_url").toString();

                    product.setImageUrl(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload to Cloudinary failed", e);
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
