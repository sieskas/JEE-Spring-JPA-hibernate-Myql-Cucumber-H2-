package com.example.demo.service;

import com.example.demo.domain.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();

    void saveUser(User user);
}
