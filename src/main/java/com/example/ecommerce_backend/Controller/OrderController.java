package com.example.ecommerce_backend.Controller;




import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.example.ecommerce_backend.Modal.Order;
import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.OrderRepository;
import com.example.ecommerce_backend.Repo.UserRepository;
import com.example.ecommerce_backend.Service.InvoiceService;
import com.example.ecommerce_backend.Service.UserService;

@RestController
//@CrossOrigin(origins = "http://127.0.0.1:5500")
@CrossOrigin(origins = "https://devwerxoil.netlify.app")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private InvoiceService invoiceService;
  
//
//    @PostMapping("/save")
//    public Order saveOrder(@RequestBody Order order) {
//        return orderRepo.save(order);
//    }
//    @PostMapping("/orders")
//    public Order createOrder(@RequestBody Order order, Principal principal) {
//        String userId = principal.getName(); // or fetch from SecurityContext / JWT
//        order.setUserId(userId);
//        return orderRepo.save(order);
//    }
    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(@RequestBody Order order, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        // Fetch the user by username (or email)
        String username = principal.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Set userId as the actual MongoDB User.id
        order.setUserId(user.getId());

        // Set order date
        order.setOrderDate(LocalDateTime.now());

        // Save order
        Order savedOrder = orderRepo.save(order);

        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable String userId) {
        return orderRepo.findByUserId(userId);
    }

  
    @GetMapping("/{orderId}/invoice")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable String orderId) throws Exception {
    	// ✅ Correct
    	var pdf = invoiceService.generateInvoice(orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=invoice_" + orderId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdf));
    }
    
  


}
