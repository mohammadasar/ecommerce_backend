package com.example.ecommerce_backend.Controller;

import com.razorpay.*;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/api/payment")
public class PaymentController {

	 @PostMapping("/create-order")
	    public Map<String, Object> createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
	        RazorpayClient client = new RazorpayClient("rzp_test_qoebkgBWvtmZQw", "6xnMk20pvCVpXkJZPCuVo7mR");

	        int amount = Integer.parseInt(data.get("amount").toString()) * 100;

	        JSONObject orderRequest = new JSONObject();
	        orderRequest.put("amount", amount);
	        orderRequest.put("currency", "INR");
	        orderRequest.put("receipt", "order_rcptid_" + new Random().nextInt(9999));

	        Order order = client.orders.create(orderRequest);

	        Map<String, Object> response = new HashMap<>();
	        response.put("id", order.get("id"));
	        response.put("amount", order.get("amount"));

	        return response;
	    }
}
