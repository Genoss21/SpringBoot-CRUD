package com.fritz.beststore.models;

import jakarta.persistence.*;
import java.util.Date;  // Use java.util.Date instead of java.sql.Date

@Entity
@Table(name = "products")
public class Product {

    @Id // Use @Id to specify the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String brand;
    private String category;
    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Date createdAt;  // Use java.util.Date
    private String imageFileName;  // Change to String to store file path or use byte[] to store the file itself

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getImageFileName() {  // Getter for file path (if changed to String)
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {  // Setter for file path (if changed to String)
        this.imageFileName = imageFileName;
    }
}
