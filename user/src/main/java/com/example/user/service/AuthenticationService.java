package com.example.user.service;

import com.example.user.entity.User;

public interface AuthenticationService {
    User authenticate(String email, String password);

    String generateToken(User user);

    boolean validateToken(String token);
}
