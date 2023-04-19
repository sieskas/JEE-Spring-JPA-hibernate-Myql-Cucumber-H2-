package com.example.demo.service;

public interface AuthentificationService {

    String hashPassword(String plainPassword);
    boolean verifyPassword(String plainPassword, String storedPassword);
}
