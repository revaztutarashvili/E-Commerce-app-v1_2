package com.example.E_Commerce.app.v1_2.service.user;

import com.example.E_Commerce.app.v1_2.dto.ApiResponse;
import com.example.E_Commerce.app.v1_2.dto.PurchaseRequestDto;
import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.model.enums.Role;
import com.example.E_Commerce.app.v1_2.service.product.ProductService;
import com.example.E_Commerce.app.v1_2.service.user.strategy.AllCartCheckoutStrategy;
import com.example.E_Commerce.app.v1_2.service.user.strategy.CheckoutStrategy;
import com.example.E_Commerce.app.v1_2.service.user.strategy.DirectCheckoutStrategy;
import com.example.E_Commerce.app.v1_2.service.user.strategy.SingleCartItemCheckoutStrategy;
import com.example.E_Commerce.app.v1_2.util.RoleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
//ეს კლასი არის მთავარი სერვისი, რომელიც იწყებს ყიდვის პროცესს და იყენებს სხვადასხვა CheckoutStrategy იმპლემენტაციას 
//(რომლებიც განთავსებულია service/user/strategy პაკეტში) 
//ყიდვის რეალური ლოგიკის შესასრულებლად, მოთხოვნის სპეციფიკის მიხედვით.

@Service
public class PurchaseService {

    private final UserSessionService userSessionService;
    private final ProductService productService;

    private final List<CheckoutStrategy> checkoutStrategies; // List of available strategies

    @Autowired
    public PurchaseService(UserSessionService userSessionService, ProductService productService, List<CheckoutStrategy> checkoutStrategies) {
        this.userSessionService = userSessionService;
        this.productService = productService;

        this.checkoutStrategies = checkoutStrategies;
    }

    /*
     * Initiates a purchase process using the appropriate strategy.
     * @param username The username.
     * @param role The user's role (must be USER).
     * @param request The purchase request details.
     * @return ApiResponse indicating the purchase outcome.
     */
    public ApiResponse checkout(String username, String role, PurchaseRequestDto request) {
        // Ensure the user is a USER
        RoleValidator.validateRole(role, Role.USER);

        // Get the user session (creates if it doesn't exist, though it should for a USER)
        UserSession userSession = userSessionService.getUserSession(username, Role.USER);

        // Find the appropriate strategy
        for (CheckoutStrategy strategy : checkoutStrategies) {
            if (strategy.canHandle(request, userSession)) {
                // Execute the strategy
                // Pass necessary dependencies to the strategy's execute method
                return strategy.execute(request, userSession, productService);
            }
        }

        // If no strategy was found to handle the request
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not determine the correct checkout method for the provided request.");
    }

    // Dedicated method for "Checkout Now" to explicitly trigger that strategy
    public ApiResponse checkoutNow(String username, String role, PurchaseRequestDto request) {
        // Ensure the user is a USER
        RoleValidator.validateRole(role, Role.USER);

        if (request == null || !request.isDirectPurchase()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request for direct checkout.");
        }

        UserSession userSession = userSessionService.getUserSession(username, Role.USER);

        // Explicitly find and use the DirectCheckoutStrategy
        for (CheckoutStrategy strategy : checkoutStrategies) {
            if (strategy instanceof DirectCheckoutStrategy) {

                return strategy.execute(request, userSession, productService);
            }
        }

        // Should not happen if DirectCheckoutStrategy is in the list, but defensive coding
        throw new IllegalStateException("DirectCheckoutStrategy bean not found.");
    }

    // Dedicated method for "Checkout All Cart" to explicitly trigger that strategy
    public ApiResponse checkoutAllCart(String username, String role) {
        // Ensure the user is a USER
        RoleValidator.validateRole(role, Role.USER);

        UserSession userSession = userSessionService.getUserSession(username, Role.USER);

        // Explicitly find and use the AllCartCheckoutStrategy
        for (CheckoutStrategy strategy : checkoutStrategies) {
            if (strategy instanceof AllCartCheckoutStrategy) {

                return strategy.execute(null, userSession, productService);
            }
        }
        // Should not happen
        throw new IllegalStateException("AllCartCheckoutStrategy bean not found.");
    }

    // Method for "Checkout Single Item from Cart"
    public ApiResponse checkoutSingleCartItem(String username, String role, PurchaseRequestDto request) {
        // Ensure the user is a USER
        RoleValidator.validateRole(role, Role.USER);

        if (request == null || !request.isSingleCartItemPurchase()) {
            // This validation needs to align with how SingleCartItemCheckoutStrategy's canHandle works
            // Let's rely on the strategy itself for the specifics of the request validation.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request for single item checkout from cart.");
        }

        UserSession userSession = userSessionService.getUserSession(username, Role.USER);

        // Explicitly find and use the SingleCartItemCheckoutStrategy
        for (CheckoutStrategy strategy : checkoutStrategies) {
            if (strategy instanceof SingleCartItemCheckoutStrategy) {

                return strategy.execute(request, userSession, productService);
            }
        }
        // Should not happen
        throw new IllegalStateException("SingleCartItemCheckoutStrategy bean not found.");
    }
}
