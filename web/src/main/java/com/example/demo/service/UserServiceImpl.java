package com.example.demo.service;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository userRepository;
    @Inject
    private UserMapper userMapper;

    @Override
    public List<User> getUsers() {
        return userMapper.entityToDomain(userRepository.findAll());
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(userMapper.domaineToEntity(user));
    }

}
