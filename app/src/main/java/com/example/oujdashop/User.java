package com.example.oujdashop;

public class User {
    private int id;
    private String name;
    private String email;
    private String imageUri;

    public User(int id, String name, String email, String imageUri) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
    }

    public User(int id, String name, String email) {
        this(id, name, email, null);
    }

    public String getFirstName() {
        String[] parts = name.split(" ", 2);
        return parts[0];
    }

    public String getLastName() {
        String[] parts = name.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getImageUri() { return imageUri; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
} 