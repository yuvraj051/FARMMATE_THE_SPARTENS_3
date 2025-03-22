package com.example.farmmate;

public class Product {
    private int id; // Unique identifier for the product
    private String mobileNumber; // Mobile number associated with the product
    private String productType; // Type of the product (e.g., vegetable, fruit)
    private String productName; // Name of the product
    private String details; // Additional details about the product
    private double price; // Price of the product
    private String imageUrl; // URL of the product image
    private String villages; // Villages where the product is available
    private int stock; // Stock quantity of the product

    // Constructor
    public Product(int id, String mobileNumber, String productType, String productName, String details,
                   double price, String imageUrl, String villages, int stock) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.productType = productType;
        this.productName = productName;
        this.details = details;
        this.price = price;
        this.imageUrl = imageUrl;
        this.villages = villages;
        this.stock = stock;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getProductType() {
        return productType;
    }

    public String getProductName() {
        return productName;
    }

    public String getDetails() {
        return details;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVillages() {
        return villages;
    }

    public int getStock() {
        return stock;
    }
}