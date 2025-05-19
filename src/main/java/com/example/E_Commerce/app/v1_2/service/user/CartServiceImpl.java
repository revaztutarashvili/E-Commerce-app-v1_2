package com.example.E_Commerce.app.v1_2.service.user;

import com.example.E_Commerce.app.v1_2.model.CartItem;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.model.enums.Role;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;
//ეს კლასი მართავს პროდუქტების დამატებას/წაშლას ამ კალათიდან (მარაგის შემოწმებით)
@Service
public class CartServiceImpl implements CartService {

    private final UserSessionService userSessionService;
    private final ProductService productService;

    @Autowired
    public CartServiceImpl(UserSessionService userSessionService, ProductService productService) {
        this.userSessionService = userSessionService;
        this.productService = productService;
    }

    @Override
    public void addToCart(String username, String role, String productId, int quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive.");
        }

        UserSession userSession = userSessionService.getUserSession(username, Role.valueOf(role.toUpperCase()));
        Product product = productService.getProductById(productId); // Throws NOT_FOUND if product doesn't exist

        // Check if adding this quantity exceeds available stock
        int currentCartQuantity = 0;
        CartItem existingItem = userSession.getCart().get(productId);
        if (existingItem != null) {
            currentCartQuantity = existingItem.getQuantity();
        }

        if (product.getStock() < (currentCartQuantity + quantity)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot add " + quantity + " of product " + product.getName() + " to cart. Total in cart would exceed available stock (" + product.getStock() + ").");
        }

        // Add/Update item in cart
        userSession.getCart().compute(productId, (k, v) -> {
            if (v == null) {
                return new CartItem(product, quantity);
            } else {
                v.setQuantity(v.getQuantity() + quantity);
                return v;
            }
        });
    }

    @Override
    public void removeFromCart(String username, String role, String productId, int quantity) {
        if (quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be positive.");
        }

        UserSession userSession = userSessionService.getUserSession(username, Role.valueOf(role.toUpperCase()));
        Map<String, CartItem> userCart = userSession.getCart();

        CartItem existingItem = userCart.get(productId);
        if (existingItem == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " not found in your cart.");
        }

        int currentQuantity = existingItem.getQuantity();
        if (quantity >= currentQuantity) {
            // Remove the item completely if requested quantity is >= current quantity
            userCart.remove(productId);
        } else {
            // Reduce the quantity
            existingItem.setQuantity(currentQuantity - quantity);
        }
    }

    @Override
    public Map<String, CartItem> getUserCart(String username, String role) {
        UserSession userSession = userSessionService.getUserSession(username, Role.valueOf(role.toUpperCase()));
        // Return an unmodifiable map to prevent external modification
        return Collections.unmodifiableMap(userSession.getCart());
    }
}
