package com.example.E_Commerce.app.v1_2.util;


import com.example.E_Commerce.app.v1_2.model.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RoleValidator {

    // Private constructor to prevent instantiation
    private RoleValidator() {
        throw new IllegalStateException("Utility class");
    }

    /*
     * Validates if the provided role matches the required role.
     * Throws ResponseStatusException (HTTP 403 Forbidden) if roles do not match.
     *
     * @param providedRole The role string provided in the request (e.g., "ADMIN", "USER").
     * @param requiredRole The Role enum required for the operation.
     */
    public static void validateRole(String providedRole, Role requiredRole) {
        if (providedRole == null || providedRole.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Role header 'X-User-Role' is required.");
        }
        try {
            Role actualRole = Role.valueOf(providedRole.trim().toUpperCase());
            if (actualRole != requiredRole) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions. Required role: " + requiredRole.name());
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid role provided: " + providedRole + ". Allowed roles: ADMIN, USER.");
        }
    }
}