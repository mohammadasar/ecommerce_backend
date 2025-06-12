package com.example.ecommerce_backend.Controller;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.UserRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
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
}
