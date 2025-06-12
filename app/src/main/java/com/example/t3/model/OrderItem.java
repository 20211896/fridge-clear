package com.example.t3.model;

import java.util.Date;
import java.util.List;

public class OrderItem {
    private String orderId;
    private long orderDate; // Date 대신 long으로 저장 (Gson 직렬화 용이)
    private int totalAmount;
    private String status;
    private List<String> orderDetails;

    public OrderItem(String orderId, Date orderDate, int totalAmount, String status, List<String> orderDetails) {
        this.orderId = orderId;
        this.orderDate = orderDate.getTime(); // Date를 long으로 변환
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderDetails = orderDetails;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public Date getOrderDate() {
        return new Date(orderDate); // long을 Date로 변환
    }

    public long getOrderDateLong() {
        return orderDate;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getOrderDetails() {
        return orderDetails;
    }

    // Setters
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate.getTime();
    }

    public void setOrderDateLong(long orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderDetails(List<String> orderDetails) {
        this.orderDetails = orderDetails;
    }
}