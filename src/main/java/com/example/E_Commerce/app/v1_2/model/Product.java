package com.example.E_Commerce.app.v1_2.model;

import java.util.Objects;
/*
1. აქ აღვწერ პროდუქტის მახასიათებლებს, გეთერ-სეთერ-კონსტრუქტორით

2. ასევე გამოვიყენებ ჰეშირებას რომ პროდუქტები შედარდეს ერთმანეთს ID-ებით
    და თუ ერთი აიდი ექნებათ, შეინახოს ერთნაირ ადგილზე, ანუ აღიქვას ერთი და იგივე პროდუქტად

3. რატომ ჰეშმეფი? სწრაფი მეთოდია პროდუქტების შესანახად, მოსაძებნად ძმაო:)
 */

public class Product {
    private String id;
    private String name;
    private double price;
    private int stock;

    // Constructors
    public Product() {
    }

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    // Getters and Setters
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // ვამოწმებს, რომ თუ სხვა ობიექტი არის Product ტიპის და მას აქვს იგივე id,
    // მაშინ ეს ორი Product ობიექტი ტოლად ჩაითვლება. ანუ გადავტვირთე ექუალს მეთოდი
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

//    აქ ობიექტს თუ აქვს ტოლი id, მათ ჰეშ კოდიც ტოლი გაუხდეთ,
//    რითაც დასრულდება equals/hashCode კონტრაქტი.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}