package com.example.oujdashop;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private String description;
    private String category;
    private int imageResId;
    private String imageUri;
    private String barcode;

    public Product() {
    }

    public Product(int id, String name, double price, String description, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.imageResId = R.drawable.baseline_shopping_cart_24;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getImageResId() { return imageResId; }
    public String getImageUri() { return imageUri; }
    public String getBarcode() {
        return barcode;
    }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
} 