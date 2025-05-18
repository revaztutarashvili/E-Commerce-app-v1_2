package com.example.E_Commerce.app.v1_2.service.user.strategy;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;

public interface CheckoutStrategy {

    /**
     * Determines if this strategy can handle the given purchase request.
     * @param request The purchase request DTO.
     * @param userSession The current user session.
     * @return true if this strategy applies, false otherwise.
     */
    boolean canHandle(PurchaseRequestDto request, UserSession userSession);

    /**
     * Executes the checkout logic for this strategy.
     * @param request The purchase request DTO.
     * @param userSession The current user session.
     * @param productService The product service to interact with stock.
     * @return An ApiResponse indicating the result of the purchase.
     */
    ApiResponse execute(PurchaseRequestDto request, UserSession userSession, ProductService productService);
}