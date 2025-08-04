package com.example.ecommerce_backend.Controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.ecommerce_backend.Modal.order;
import com.example.ecommerce_backend.Repo.OrderRepository;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepo;

    @PostMapping("/save")
    public order saveOrder(@RequestBody order order) {
        return orderRepo.save(order);
    }
}
