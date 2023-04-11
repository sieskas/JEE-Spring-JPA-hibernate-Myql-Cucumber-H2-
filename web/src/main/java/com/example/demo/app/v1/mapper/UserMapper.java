package com.example.demo.app.v1.mapper;

import com.example.demo.app.v1.resources.UserResource;
import com.example.demo.domain.User;
import com.example.demo.repository.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface UserMapper {
    User entityToDomain(UserEntity user);

    List<User> entityToDomain(List<UserEntity> user);


    UserResource domainToResource(User user);

    List<UserResource> domainToResource(List<User> user);

    User resourceToDomain(UserResource user);

    UserEntity domaineToEntity(User user);

}