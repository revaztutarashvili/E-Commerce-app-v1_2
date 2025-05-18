package com.example.E_Commerce.app.v1_2.controller;


import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.ProductDto;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.enums.Role;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/products")
public class AdminController {

    private final ProductService productService;

    @Autowired
    public AdminController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add-new-product")
    public ResponseEntity<ApiResponse<ProductDto>> addProduct(
             @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @RequestBody ProductDto productDto) {

        RoleValidator.validateRole(role, Role.ADMIN);

        try {
            // ვაკონვერტებ DTO  Model-ად
            Product product = new Product(productDto.getId(), productDto.getName(), productDto.getPrice(), productDto.getStock());
            Product addedProduct = productService.addProduct(product);

            // ვაკონვერტებ Model-ს უკან,  DTO-ოდ რათა მივიღო response
            ProductDto responseDto = new ProductDto(addedProduct.getId(), addedProduct.getName(), addedProduct.getPrice(), addedProduct.getStock());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Product added successfully", responseDto));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/edit-product{id}")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @PathVariable String id,
            @RequestBody ProductDto productDto) {

        RoleValidator.validateRole(role, Role.ADMIN);

        try {
            // ვაკონვერტებ DTO-ს  Model-ად (ანუ ID მომაქვს path-დან, details მომაქვს body-დან)
            Product product = new Product(id, productDto.getName(), productDto.getPrice(), productDto.getStock());
            Product updatedProduct = productService.updateProduct(id, product);

            // ვაკონვერტებ Model-ს უკან,  DTO-ოდ რათა მივიღო response
            ProductDto responseDto = new ProductDto(updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getStock());

            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", responseDto));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-product{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @PathVariable String id) {

        RoleValidator.validateRole(role, Role.ADMIN);

        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/product-list")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name) {

        RoleValidator.validateRole(role, Role.ADMIN); // Assuming product listing is an admin function here per explicit role check requirement

        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDto> productDtos = products.stream()
                    .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productDtos));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
}