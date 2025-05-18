package com.example.E_Commerce.app.v1_2.service.user.strategy;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.CartItem;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.util.BudgetValidator; // საჭიროა სტატიკური მეთოდის გამოძახებისთვის
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Component("allCartCheckoutStrategy")
public class AllCartCheckoutStrategy implements CheckoutStrategy {

    @Override
    public boolean canHandle(PurchaseRequestDto request, UserSession userSession) {
        // ეს ლოგიკა რჩება იგივე, როგორც ადრე განსაზღვრეთ
        return request == null || (request.getProductId() == null && request.getQuantity() == null);
    }

    @Override
    // BudgetValidator budgetValidator პარამეტრი ამოღებულია
    public ApiResponse execute(PurchaseRequestDto request, UserSession userSession, ProductService productService) {
        Map<String, CartItem> userCart = userSession.getCart();

        if (userCart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your cart is empty.");
        }

        double totalCost = 0;

        // 1. Calculate total cost and perform stock checks for all items FIRST
        for (Map.Entry<String, CartItem> entry : userCart.entrySet()) {
            CartItem item = entry.getValue();
            // დარწმუნდით, რომ პროდუქტი ჯერ კიდევ არსებობს
            Product product = productService.getProductById(item.getProduct().getId());

            // შეამოწმეთ მარაგი კალათაში არსებული რაოდენობისთვის
            productService.checkStock(product.getId(), item.getQuantity()); // აგდებს შეცდომას თუ მარაგი არასაკმარისია

            totalCost += item.getTotalPrice();
        }

        // 2. Check total budget
        // გამოიძახეთ სტატიკური მეთოდი პირდაპირ კლასის სახელით
        BudgetValidator.checkBudget(userSession.getBudget(), totalCost); // აგდებს შეცდომას თუ ბიუჯეტი არასაკმარისია

        // 3. Perform purchase actions (update budget and stock for all items, clear cart)
        userSession.setBudget(userSession.getBudget() - totalCost);

        for (Map.Entry<String, CartItem> entry : userCart.entrySet()) {
            CartItem item = entry.getValue();
            // შეამცირეთ მარაგი თითოეული შეძენილი ნივთისთვის
            productService.updateStock(item.getProduct().getId(), -item.getQuantity());
        }

        // დაცალეთ კალათა წარმატებული შეძენის შემდეგ
        userCart.clear();

        // 4. Return success response
        return ApiResponse.success("Successfully purchased all items in the cart. Total cost: " + totalCost + ". Remaining budget: " + userSession.getBudget());
    }
}