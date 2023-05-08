package com.example.demo.repository;

import com.example.demo.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity getUserEntityByEmail(String email);
    List<UserEntity> findAll();

}
