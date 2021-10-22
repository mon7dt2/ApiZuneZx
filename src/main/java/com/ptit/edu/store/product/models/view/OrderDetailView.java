package com.ptit.edu.store.product.models.view;

import com.ptit.edu.store.product.models.data.Order;
import com.ptit.edu.store.product.models.data.OrderDetail;
import com.ptit.edu.store.product.models.data.Product;

public class OrderDetailView {
    private String id;

    private String orderId;
    private String productName;
    private String productAvatarUrl;
    private int quantity;
    private double price;
    private double total;
    private String size;

    public OrderDetailView(OrderDetail detail) {
        this.id = detail.getId();
        this.orderId = detail.getOrder().getId();
        this.productName = detail.getProduct().getName();
        this.productAvatarUrl = detail.getProduct().getAvatarUrl();
        this.quantity = detail.getQuantity();
        this.price = detail.getPrice();
        this.total = (double) quantity * price;
        this.size = detail.getSize();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductAvatarUrl() {
        return productAvatarUrl;
    }

    public void setProductAvatarUrl(String productAvatarUrl) {
        this.productAvatarUrl = productAvatarUrl;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
