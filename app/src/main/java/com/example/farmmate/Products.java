package com.example.farmmate;

public class Products {
    private String id;
    private String productName;
    private String stock;

    public Products(String id, String productName, String stock) {
        this.id = id;
        this.productName = productName;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getStock() {
        return stock;
    }
}