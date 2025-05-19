package com.example.E_Commerce.app.v1_2.service.user.strategy;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.util.BudgetValidator;
import org.springframework.stereotype.Component;

@Component("directCheckoutStrategy")
public class DirectCheckoutStrategy implements CheckoutStrategy {

    @Override
    public boolean canHandle(PurchaseRequestDto request, UserSession userSession) {
        
        return request != null && request.isDirectPurchase();
    }

    @Override
   
    public ApiResponse execute(PurchaseRequestDto request, UserSession userSession, ProductService productService) {
        String productId = request.getProductId();
        int quantity = request.getQuantity();

        // 1. მომაქვს პროდუქტები სიიდან
        Product product = productService.getProductById(productId); // აგდებს შეცდომას თუ არ არსებობს

        // 2. აქ ვამოწმებ მარაგს, ნაშთს
        productService.checkStock(productId, quantity); // აგდებს შეცდომას თუ მარაგი არასაკმარისია

        // 3. გამოვითვლი ღირებულებას
        double totalCost = product.getPrice() * quantity;

        // 4.შევამოწმებ ბიუჯეტი რამდენი აქვს იუზერს 
      
        BudgetValidator.checkBudget(userSession.getBudget(), totalCost); // აგდებს შეცდომას თუ ბიუჯეტი არასაკმარისია

        // 5. ვასრულებ შეძენის ოპერაციას ანუ(განვაახლებ ბიუჯეტს და ასევე ნაშთსაც)
        userSession.setBudget(userSession.getBudget() - totalCost);
        // მარაგი უკვე შემოწმებულია checkStock-ში, updateStock კიიი ამცირებს რაოდენობასსსსსს
        productService.updateStock(productId, -quantity); // აქ ვამცირებ მარაგსსსსს

        // 6. ვაბრუნებ რომ წარმატებით დასრულდა ოპერაცია
        return ApiResponse.success("Successfully purchased " + quantity + " of " + product.getName() + " directly. Remaining budget: " + userSession.getBudget());
    }
}
