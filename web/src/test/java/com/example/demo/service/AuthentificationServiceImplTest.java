package com.example.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthentificationServiceImplTest {

    @InjectMocks
    private AuthentificationServiceImpl passwordService;


    @Test
    void testHashPassword() {
        String plainPassword = "test_password";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
    }

    @Test
    void testVerifyPassword() {
        String plainPassword = "test_password";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        assertTrue(passwordService.verifyPassword(plainPassword, hashedPassword));
    }

    @Test
    void testVerifyPasswordFail() {
        String plainPassword = "test_password";
        String wrongPassword = "wrong_password";
        String hashedPassword = passwordService.hashPassword(plainPassword);

        assertFalse(passwordService.verifyPassword(wrongPassword, hashedPassword));
    }

//    @Test
//    void sdada() {
//        String hashedPassword = passwordService.hashPassword("1234");
//
//        assertFalse(passwordService.verifyPassword("1234", hashedPassword));
//    }
}