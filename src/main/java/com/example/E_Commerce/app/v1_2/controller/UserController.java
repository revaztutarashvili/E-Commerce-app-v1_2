package com.example.E_Commerce.app.v1_2.controller;


import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.ProductDto;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.CartItem;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.model.enums.Role;
import com.example.E_Commerce.app.v1_2.service.product.ProductServiceImpl;
import com.example.E_Commerce.app.v1_2.service.user.CartService;
import com.example.E_Commerce.app.v1_2.service.user.PurchaseService;
import com.example.E_Commerce.app.v1_2.service.user.UserSessionService;
import com.example.E_Commerce.app.v1_2.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    private final CartService cartService;
    private final PurchaseService purchaseService;
    private final UserSessionService userSessionService;
    private final ProductServiceImpl productServiceImpl;

    @Autowired
    public UserController(CartService cartService, PurchaseService purchaseService, UserSessionService userSessionService, ProductServiceImpl productServiceImpl) {
        this.cartService = cartService;
        this.purchaseService = purchaseService;
        this.userSessionService = userSessionService;
        this.productServiceImpl = productServiceImpl;
    }
// product list ენდფოინთი
    @GetMapping("/products-list")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProductsForUser(
        @RequestHeader("User-Role") String role,
        @RequestHeader("User-Name") String name) {

        RoleValidator.validateRole(role, Role.USER);

        try {
            List<Product> products = productServiceImpl.getAllProducts(); //
            List<ProductDto> productDtos = products.stream()
                    .map(p -> new ProductDto(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                    .collect(Collectors.toList()); //გადავაქცევ Product-ებს ProductDto-ებად
            return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productDtos));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // საბარათე ოპერაციების ენდფოინთები რაც მიბმულია Cart-თან

    @PostMapping("/add-to-cart-single-product")
    public ResponseEntity<ApiResponse<Void>> addToCart(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @RequestParam String productId,
            @RequestParam int quantity) {

        RoleValidator.validateRole(role, Role.USER);

        try {
            cartService.addToCart(name, role, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Product added to cart successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @DeleteMapping("/prod-remove-from-cart/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @PathVariable String productId,
            @RequestParam int quantity) { // ნებას ვრთავ, თუ ერთი სახის რამდენიმე პროდუქტი აქვს ბარათზე წაშალოს იმ პროდუქტის სასურველი რაოდენობა.

        RoleValidator.validateRole(role, Role.USER);

        try {
            cartService.removeFromCart(name, role, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Product removed from cart successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/see-added-prods-on-cart")
    public ResponseEntity<ApiResponse<Map<String, CartItem>>> getUserCart(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name) {

        RoleValidator.validateRole(role, Role.USER);

        try {
            Map<String, CartItem> cart = cartService.getUserCart(name, role);
            return ResponseEntity.ok(ApiResponse.success("Cart contents retrieved successfully", cart));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    // შეძენასთან დაკავშრებული ენდფოინთები
//checkoutNow ფუნქცია მომხმარებელს საშუალებას აძლევს ნივთი შეიძინოს პირდაპირ, კალათში დამატების გარეშე.
    @PostMapping("/checkout-now")
    public ResponseEntity<ApiResponse<?>> checkoutNow(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @RequestBody PurchaseRequestDto requestDto) { // უეჭ უნდა შეიცავდეს პროდუქტის ID-ს და რაოდენობას

        RoleValidator.validateRole(role, Role.USER);

        try {
            ApiResponse<?> response = purchaseService.checkoutNow(name, role, requestDto);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/checkout-all-on cart")
    public ResponseEntity<ApiResponse<?>> checkoutAllCart(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name) {

        RoleValidator.validateRole(role, Role.USER);

        try {
            ApiResponse<?> response = purchaseService.checkoutAllCart(name, role);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/checkout-single-item-on-cart")
    public ResponseEntity<ApiResponse<?>> checkoutSingleCartItem(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name,
            @RequestBody PurchaseRequestDto requestDto) { // Should contain productId and quantity

        RoleValidator.validateRole(role, Role.USER);

        try {
            ApiResponse<?> response = purchaseService.checkoutSingleCartItem(name, role, requestDto);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/user-budget-now")
    public ResponseEntity<ApiResponse<Double>> getUserBudget(
            @RequestHeader("User-Role") String role,
            @RequestHeader("User-Name") String name) {

        RoleValidator.validateRole(role, Role.USER);

        try {
            UserSession userSession = userSessionService.getUserSession(name, Role.USER);
            return ResponseEntity.ok(ApiResponse.success("Current budget retrieved successfully", userSession.getBudget()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("An unexpected error occurred: " + e.getMessage()));
        }
    }
}