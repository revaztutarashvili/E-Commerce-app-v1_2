package com.example.E_Commerce.app.v1_2.util;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BudgetValidator {

    // Private constructor to prevent instantiation
    private BudgetValidator() {
        throw new IllegalStateException("Utility class");
    }

    /*
     * ვამოწმებთ შეესაბამება თუ არა მითითებულ ფასს იუზერის ბიუჯეტი
     * Throws ResponseStatusException (HTTP 400 Bad Request) if budget is insufficient.
     *
     * @param currentBudget The user's current budget.
     * @param cost The total cost of the items to purchase.
     */
    public static void checkBudget(double currentBudget, double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }
        if (currentBudget < cost) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient budget. Current budget: " + currentBudget + ", Required: " + cost);
        }
    }
}
