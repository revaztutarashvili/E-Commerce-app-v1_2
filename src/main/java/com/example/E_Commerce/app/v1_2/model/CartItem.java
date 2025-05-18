package com.example.E_Commerce.app.v1_2.model;

//აქ აღვწერ თუ რა მონაცემები მჭირდება ბარათზე დამატებული პროდუქტებისა (რაოდენობა, პროდუქტის მახასიათებლები)
//გავწერ კონტრუქტორს, გეთერებს სეთერებს და ასეეეეეეე....
public class CartItem {
    private Product product;
    private int quantity;

    // Constructors
    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }
}
