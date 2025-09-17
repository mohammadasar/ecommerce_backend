package com.example.ecommerce_backend.RequestDto;


import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDto {
    private List<ProductItem> products;
    private String paymentType;  // COD / ONLINE
    private String status;       // PENDING / PAID
    private String orderId;      // for Razorpay (optional)
    private String paymentId;    // for Razorpay (optional)
    

    public List<ProductItem> getProducts() {
		return products;
	}


	public void setProducts(List<ProductItem> products) {
		this.products = products;
	}


	public String getPaymentType() {
		return paymentType;
	}


	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getOrderId() {
		return orderId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getPaymentId() {
		return paymentId;
	}


	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}


	@Data
    public static class ProductItem {
        private String name;
        private double price;
        private int quantity;
        private String imageUrl; // optional
        
		
	
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		
        
        
    }
}
