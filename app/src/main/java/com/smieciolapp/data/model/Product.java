package com.smieciolapp.data.model;

public class Product {

    private String id;
    private String name;
    private double weight;
    private boolean confirmed;
    private String barcode;

    public Product(String id, String name, double weight, boolean confirmed, String barcode) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.confirmed = confirmed;
        this.barcode = barcode;
    }

    public Product() {
        this.id = "";
        this.name = "";
        this.weight = 0.0;
        this.confirmed = false;
        this.barcode = "";
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

    @Override
    public String toString() {
        return name;
    }
}
