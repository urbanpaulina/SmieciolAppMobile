//Klasa potrzebna do "proszenia o produkt" zanim wykona się funkcja asynchroniczna

package com.smieciolapp.data.model;

public interface MyCallback {
    void onCallback(Product product);
}