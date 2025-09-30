package com.example.ecommerce_backend.Controller;




import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.example.ecommerce_backend.Config.TwilioProperties;
import com.example.ecommerce_backend.Modal.Order;
import com.example.ecommerce_backend.Modal.User;
import com.example.ecommerce_backend.Repo.OrderRepository;
import com.example.ecommerce_backend.Repo.UserRepository;
import com.example.ecommerce_backend.RequestDto.OrderRequestDto;
import com.example.ecommerce_backend.Service.InvoiceService;
import com.example.ecommerce_backend.Service.ReportService;
import com.example.ecommerce_backend.Service.UserService;
import com.example.ecommerce_backend.Service.WhatsAppService;

@RestController
//@CrossOrigin(origins = "http://127.0.0.1:5500")
@CrossOrigin(origins = "https://devwerxoil.netl/ify.app")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private WhatsAppService whatsappService;

    @Autowired
    private TwilioProperties twilioProperties;
    
  
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
//    @PostMapping("/save")
//    public ResponseEntity<?> saveOrder(@RequestBody Order order, Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//        }
//
//        // Fetch the user by username (or email)
//        String username = principal.getName();
//        User user = userRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//        // Set userId as the actual MongoDB User.id
//        order.setUserId(user.getId());
//
//        // Set order date
//        order.setOrderDate(LocalDateTime.now());
//
//        // Save order
//        Order savedOrder = orderRepo.save(order);
//
//        return ResponseEntity.ok(savedOrder);
//    }
   
    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(@RequestBody Order order, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String username = principal.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        order.setUserId(user.getId());
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepo.save(order);

        // ✅ Build WhatsApp message
        String invoiceLink = "http://localhost:8080/api/orders/" + savedOrder.getId() + "/invoice";
        String messageText = "Hello " + user.getUsername() +
                ", your order has been placed successfully!\n\n" +
                "🛒 Product: " + savedOrder.getProductName() + "\n" +
                "📦 Quantity: " + savedOrder.getQuantity() + "\n" +
                "💰 Total Amount: ₹" + (savedOrder.getPrice() * savedOrder.getQuantity()) + "\n" +
                "📅 Order Date: " + savedOrder.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")) + "\n\n" +
                "📄 Download your invoice: " + invoiceLink;


        // ✅ Fix phone number formatting
        String phoneWithCode = user.getPhone().startsWith("+")
                ? user.getPhone()
                : "+91" + user.getPhone();

        // ✅ Debug logs (check in console before sending)
        System.out.println("From: " + twilioProperties.getWhatsappNumber());
        System.out.println("To: whatsapp:" + phoneWithCode);

        // ✅ Send WhatsApp notification
        whatsappService.sendWhatsAppMessage(phoneWithCode, messageText);

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
    
//    @PostMapping("/save-bulk")
//    public ResponseEntity<?> saveBulkOrder(@RequestBody OrderRequestDto orderRequest, Principal principal) {
//        if (principal == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
//        }
//
//        String username = principal.getName();
//        User user = userRepo.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//        // Save each product as a separate Order document
//        for (OrderRequestDto.ProductItem item : orderRequest.getProducts()) {
//            Order order = new Order();
//            order.setUserId(user.getId());
//            order.setProductName(item.getName());
//            order.setPrice(item.getPrice());
//            order.setQuantity(item.getQuantity());
//            order.setOrderDate(LocalDateTime.now());
//            order.setPaymentType(orderRequest.getPaymentType());
//            order.setStatus(orderRequest.getStatus());
//            order.setOrderId(orderRequest.getOrderId());   // for Razorpay
//            order.setPaymentId(orderRequest.getPaymentId()); // for Razorpay
//
//            orderRepo.save(order);
//        }
//
//        return ResponseEntity.ok("Order placed successfully!");
//    }
    @PostMapping("/save-bulk")
    public ResponseEntity<?> saveBulkOrder(@RequestBody OrderRequestDto orderRequest, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        String username = principal.getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Hello ").append(user.getUsername())
                      .append(", your order has been placed successfully!\n\n");

        double totalAmount = 0.0;

        // Save each product as a separate Order document
        for (OrderRequestDto.ProductItem item : orderRequest.getProducts()) {
            Order order = new Order();
            order.setUserId(user.getId());
            order.setProductName(item.getName());
            order.setPrice(item.getPrice());
            order.setQuantity(item.getQuantity());
            order.setOrderDate(LocalDateTime.now());
            order.setPaymentType(orderRequest.getPaymentType());
            order.setStatus(orderRequest.getStatus());
            order.setOrderId(orderRequest.getOrderId());   // for Razorpay
            order.setPaymentId(orderRequest.getPaymentId()); // for Razorpay

            
            Order savedOrder = orderRepo.save(order);

            // Append product info to the message
            String invoiceLink = "http://localhost:8080/api/orders/" + savedOrder.getId() + "/invoice";
            messageBuilder.append("🛒 Product: ").append(item.getName()).append("\n")
                          .append("📦 Quantity: ").append(item.getQuantity()).append("\n")
                          .append("💰 Price: ₹").append(item.getPrice()).append("\n\n");
            messageBuilder.append("📄 Invoice: ").append(invoiceLink).append("\n\n");

            totalAmount += item.getPrice() * item.getQuantity();
        }

        messageBuilder.append("💰 Total Amount: ₹").append(totalAmount).append("\n");
        messageBuilder.append("📅 Order Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append("\n");
        

        // ✅ Fix phone number formatting
        String phoneWithCode = user.getPhone().startsWith("+")
                ? user.getPhone()
                : "+91" + user.getPhone();

        // ✅ Debug logs (check in console before sending)
        System.out.println("From: " + twilioProperties.getWhatsappNumber());
        System.out.println("To: whatsapp:" + phoneWithCode); 
        
        whatsappService.sendWhatsAppMessage(phoneWithCode, messageBuilder.toString());

        return ResponseEntity.ok("Order placed successfully!");
    }

//   sales report api --->
    @GetMapping("/sales")
    public ResponseEntity<?> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        return ResponseEntity.ok(reportService.getSalesReport(startDate, endDate));
    }

}
