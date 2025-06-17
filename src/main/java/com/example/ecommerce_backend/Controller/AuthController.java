package com.example.ecommerce_backend.Controller;



import lombok.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.UserRepository;
import com.example.ecommerce_backend.Security.JwtUtil;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;


//    @PostMapping("/signup")
//    public ResponseEntity<String> registerUser(@RequestBody AuthRequest request) {
//        if (repo.findByUsername(request.getUsername()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail()); // ✅ Add this line
//        user.setPassword(encoder.encode(request.getPassword()));
//        user.setRoles(Set.of("USER"));
//
//        repo.save(user);
//        return ResponseEntity.ok("Signup successful");
//    }
    
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody AuthRequest request) {
        if (repo.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        // ✅ Use roles from request, default to USER if null or empty
        Set<String> roles = request.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Set.of("USER");
        }
        user.setRoles(roles);

        repo.save(user);
        return ResponseEntity.ok("Signup successful");
    }




    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        User user = repo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRoles());

        return Map.of("token", token);
    }
    
  

}


@Data
class AuthRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
    
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
    
}

@Data
class SignupRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
    
}


