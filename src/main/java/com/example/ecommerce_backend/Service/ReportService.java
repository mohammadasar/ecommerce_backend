package com.example.ecommerce_backend.Service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce_backend.Modal.Order;
import com.example.ecommerce_backend.Repo.OrderRepository;


@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    public Map<String, Object> getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByOrderDateBetweenAndStatus(startDate, endDate, "PAID");

        double totalRevenue = orders.stream()
                                    .mapToDouble(o -> o.getPrice() * o.getQuantity())
                                    .sum();
        long totalOrders = orders.size();
        long totalProducts = orders.stream()
                                   .mapToLong(Order::getQuantity)
                                   .sum();

        Map<String, Object> report = new HashMap<>();
        report.put("totalOrders", totalOrders);
        report.put("totalProductsSold", totalProducts);
        report.put("totalRevenue", totalRevenue);
        report.put("orders", orders);

        return report;
    }
}

