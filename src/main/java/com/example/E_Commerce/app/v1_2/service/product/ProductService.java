package com.example.E_Commerce.app.v1_2.service.product;


import com.example.E_Commerce.app.v1_2.model.Product;

import java.util.List;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, Product productDetails);
    void deleteProduct(String id);
    Product getProductById(String id);
    List<Product> getAllProducts();
    boolean updateStock(String productId, int quantityChange); // Returns true if successful, false if product not found or stock goes negative
    void checkStock(String productId, int quantity); // Throws exception if stock is insufficient
}