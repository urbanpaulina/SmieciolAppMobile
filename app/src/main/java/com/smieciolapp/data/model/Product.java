package com.smieciolapp.data.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String id;
    private String name;
    private double weight;
    private boolean confirmed;
    private String barcode;
    private String category;

    public Product(String id, String name, double weight, boolean confirmed, String barcode, String category) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.confirmed = confirmed;
        this.barcode = barcode;
        this.category = category;
    }

    public Product() {
        this.id = "";
        this.name = "";
        this.weight = 0.0;
        this.confirmed = false;
        this.barcode = "";
        this.category = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return name;
    }
}
