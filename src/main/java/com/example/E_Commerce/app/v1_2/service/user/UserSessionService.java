package com.example.E_Commerce.app.v1_2.service.user;


import com.example.E_Commerce.app.v1_2.model.UserSession;
import com.example.E_Commerce.app.v1_2.model.enums.Role;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionService {

    // მოკლედ, ეს კლასი მართავს მომხმარებლის დროებით მონაცემებს (კალათა, ბიუჯეტი) მეხსიერებაში. 
    // Key: Role:Username
    private final Map<String, UserSession> userSessions = new ConcurrentHashMap<>();

/*
     * Gets or creates a UserSession for a given username and role.
     * @param username The username.
     * @param role The user's role.
     * @return The existing or newly created UserSession.
     */
    public UserSession getUserSession(String username, Role role) {
        String key = UserSession.generateKey(username, role);
        // computeIfAbsent is thread-safe
        return userSessions.computeIfAbsent(key, k -> new UserSession(username, role));
    }

    /*
     * Retrieves an existing UserSession. Returns null if not found.
     * @param username The username.
     * @param role The user's role.
     * @return The existing UserSession or null.
     */
    public UserSession findUserSession(String username, Role role) {
        String key = UserSession.generateKey(username, role);
        return userSessions.get(key);
    }
}
