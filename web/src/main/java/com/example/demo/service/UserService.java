package com.example.demo.service;

import com.example.demo.domain.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    void saveUser(User user, String password);

    User deleteUserByEmail(String email);

    boolean authenticateUser(String username, String password);
}
