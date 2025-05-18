package com.example.E_Commerce.app.v1_2.model;


import com.example.E_Commerce.app.v1_2.model.enums.Role;

import java.util.HashMap;
import java.util.Map;

public class UserSession {
    private String username;
    private Role role;
    private double budget;
    private Map<String, CartItem> cart; // Key: გავხადე product ID, რაც გამიმარტივებს პროდუქტის მოძებნას

    // Constructor
    public UserSession(String username, Role role) {
        this.username = username;
        this.role = role;
        this.budget = 1000.0; // განვსაზღვრე ბიუჯეტი
        this.cart = new HashMap<>(); // ბარათი გავაჰეშმეფე
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Map<String, CartItem> getCart() {
        return cart;
    }

    public void setCart(Map<String, CartItem> cart) {
        this.cart = cart;
    }

    // აქ ვაგენერირებ Helper კლასით method რომ ავაგენერირო ROLE და USERNAME
//    ამას იმიტომ ვაკეთებ რომ უცებ მოხდეს ამ ორი ვალიდატორით გადამოწმება, რადგან ყველა რექვესთზე ვიყენებ
//    და მარტივი რომ გახდეს მოძიება (ეს ქეი გენერირდება UserService კლასში)
    public static String generateKey(String username, Role role) {
        return role.name() + ":" + username;
    }
}
