package com.example.demo.repository;

import com.example.demo.repository.entity.UserEntity;

import java.util.List;

public interface UserRepository {

    List<UserEntity> findAll();

    Integer save(UserEntity domaineToEntity);
}
