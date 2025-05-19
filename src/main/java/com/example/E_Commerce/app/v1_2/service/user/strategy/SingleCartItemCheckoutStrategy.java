package com.example.E_Commerce.app.v1_2.service.user.strategy;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.CartItem;
import com.example.E_Commerce.app.v1_2.model.Product;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.util.BudgetValidator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Component("singleCartItemCheckoutStrategy")
public class SingleCartItemCheckoutStrategy implements CheckoutStrategy {

    @Override
    public boolean canHandle(PurchaseRequestDto request, UserSession userSession) {
       
        return request != null && request.getProductId() != null && request.getQuantity() != null && request.getQuantity() > 0;
    }

    @Override
    
    public ApiResponse execute(PurchaseRequestDto request, UserSession userSession, ProductService productService) {
        String productId = request.getProductId();
        int quantityToBuy = request.getQuantity();
        Map<String, CartItem> userCart = userSession.getCart();

        CartItem cartItem = userCart.get(productId);
        if (cartItem == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with ID " + productId + " not found in your cart.");
        }

        int quantityInCart = cartItem.getQuantity();
        if (quantityToBuy > quantityInCart) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You only have " + quantityInCart + " of product " + cartItem.getProduct().getName() + " in your cart. Cannot buy " + quantityToBuy + ".");
        }

        Product product = cartItem.getProduct(); // მიიღეთ პროდუქტის დეტალები კალათის ელემენტიდან

        // 1. Check stock (ჯამური მარაგის წინააღმდეგ)
        // მნიშვნელოვანია: შესასყიდი რაოდენობა (quantityToBuy) ხელმისაწვდომი უნდა იყოს ჯამურ მარაგში.
        productService.checkStock(product.getId(), quantityToBuy); // აგდებს შეცდომას თუ მარაგი არასაკმარისია

        // 2. Calculate cost
        double totalCost = product.getPrice() * quantityToBuy;

        // 3. Check budget
        // გამოიძახეთ სტატიკური მეთოდი პირდაპირ კლასის სახელით
        BudgetValidator.checkBudget(userSession.getBudget(), totalCost); // აგდებს შეცდომას თუ ბიუჯეტი არასაკმარისია

        // 4. Perform purchase actions (update budget and stock, update/remove item from cart)
        userSession.setBudget(userSession.getBudget() - totalCost);

        // შეამცირეთ მარაგი
        productService.updateStock(product.getId(), -quantityToBuy);

        // განაახლეთ/ამოშალეთ ელემენტი კალათიდან
        if (quantityToBuy == quantityInCart) {
            userCart.remove(productId); // ამოშალეთ ელემენტი თუ ყიდულობთ ყველაფერს კალათიდან
        } else {
            cartItem.setQuantity(quantityInCart - quantityToBuy); // შეამცირეთ რაოდენობა კალათაში
        }

        // 5. Return success response
        return ApiResponse.success("Successfully purchased " + quantityToBuy + " of " + product.getName() + " from your cart. Remaining budget: " + userSession.getBudget());
    }
}
