package com.example.E_Commerce.app.v1_2.service.product;


import com.example.E_Commerce.app.v1_2.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductServiceImpl implements ProductService {

    // პროდუქტებს ვა-MAP-ებ, და ID გადავცემ ქეიდ(ვიყენებ მემორიში საწყის ეტაპზე რომ იყოს სტოკად რამე პროდუქტები
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    // აპის გაშვებისას რომ იყოს სატესტოდ რამდენიმე პროდუქტი
    public ProductServiceImpl() {
        products.put("1", new Product("1", "Laptop", 1200.50, 10));
        products.put("2", new Product("2", "Mouse", 25.00, 200));
        products.put("3", new Product("3", "Keyboard", 75.00, 50));
    }

    @Override
    public Product addProduct(Product product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID is required.");
        }
        if (products.containsKey(product.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with ID " + product.getId() + " already exists.");
        }
        // უშუალოდ პროდუქტის დამატების ოპერაცია
        Product newProduct = new Product(product.getId(), product.getName(), product.getPrice(), product.getStock());
        products.put(newProduct.getId(), newProduct);
        return newProduct;
    }

    @Override
    public Product updateProduct(String id, Product productDetails) {
//      ვამოწმებ მითითებული აიდით თუ მაქვს პროდუქტი
        Product existingProduct = products.get(id);
        if (existingProduct == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found.");
        }

        // ვეძებ აიდით და ვასინქრონიზებ სახელს, ფასს, ნაშთს
        synchronized (existingProduct) {
            // For atomic update within ConcurrentHashMap value:
            products.computeIfPresent(id, (k, v) -> {
                v.setName(productDetails.getName());
                v.setPrice(productDetails.getPrice());
                v.setStock(productDetails.getStock());
                return v;
            });
        }

        return products.get(id); // ვაბრუნებ დააფდეითებულ პროდუქტს
    }

    @Override
    public void deleteProduct(String id) {
        if (!products.containsKey(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found.");
        }
        products.remove(id);
    }

    @Override
    public Product getProductById(String id) {
        Product product = products.get(id);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + id + " not found.");
        }
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    @Override
    public synchronized boolean updateStock(String productId, int quantityChange) {
        // Synchronize this method to prevent race conditions during stock updates
        Product product = products.get(productId);
        if (product == null) {
            return false; // Product not found
        }
        int currentStock = product.getStock();
        int newStock = currentStock + quantityChange; // quantityChange can be negative for purchase

        if (newStock < 0) {
            // This check should ideally happen *before* calling updateStock for purchases,
            // but added here as a safeguard.
            return false; // Insufficient stock
        }

        product.setStock(newStock); // Update the stock in the map's object
        return true;
    }

    @Override
    public void checkStock(String productId, int quantity) {
        Product product = products.get(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID " + productId + " not found.");
        }
        if (product.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for product " + product.getName() + ". Available: " + product.getStock() + ", Requested: " + quantity);
        }
    }
}
