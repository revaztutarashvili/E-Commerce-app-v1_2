package com.example.E_Commerce.app.v1_2.service.user;
import com.example.E_Commerce.app.v1_2.model.CartItem;
import java.util.Map;
// ეს ინტერფეისი აკონტრაქტებს  პროდუქტების დამატებას/წაშლას კალათიდან 
public interface CartService {
    void addToCart(String username, String role, String productId, int quantity);
    void removeFromCart(String username, String role, String productId, int quantity);
    Map<String, CartItem> getUserCart(String username, String role);
}
