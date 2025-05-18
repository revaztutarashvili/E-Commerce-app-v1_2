package com.example.E_Commerce.app.v1_2.service.user.strategy;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.util.BudgetValidator; // საჭიროა სტატიკური მეთოდის გამოძახებისთვის
import org.springframework.stereotype.Component;

@Component("directCheckoutStrategy")
public class DirectCheckoutStrategy implements CheckoutStrategy {

    @Override
    public boolean canHandle(PurchaseRequestDto request, UserSession userSession) {
        // ეს ლოგიკა რჩება იგივე
        return request != null && request.isDirectPurchase();
    }

    @Override
    // BudgetValidator budgetValidator პარამეტრი ამოღებულია
    public ApiResponse execute(PurchaseRequestDto request, UserSession userSession, ProductService productService) {
        String productId = request.getProductId();
        int quantity = request.getQuantity();

        // 1. Get product
        Product product = productService.getProductById(productId); // აგდებს შეცდომას თუ არ არსებობს

        // 2. Check stock
        productService.checkStock(productId, quantity); // აგდებს შეცდომას თუ მარაგი არასაკმარისია

        // 3. Calculate cost
        double totalCost = product.getPrice() * quantity;

        // 4. Check budget
        // გამოიძახეთ სტატიკური მეთოდი პირდაპირ კლასის სახელით
        BudgetValidator.checkBudget(userSession.getBudget(), totalCost); // აგდებს შეცდომას თუ ბიუჯეტი არასაკმარისია

        // 5. Perform purchase actions (update budget and stock)
        userSession.setBudget(userSession.getBudget() - totalCost);
        // მარაგი უკვე შემოწმებულია checkStock-ში, updateStock ამცირებს რაოდენობას
        productService.updateStock(productId, -quantity); // შეამცირეთ მარაგი

        // 6. Return success response
        return ApiResponse.success("Successfully purchased " + quantity + " of " + product.getName() + " directly. Remaining budget: " + userSession.getBudget());
    }
}