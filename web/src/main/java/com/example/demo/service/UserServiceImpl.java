package com.example.demo.service;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthentificationService authentificationService;

    @Override
    public List<User> getUsers() {
        return userMapper.entityToDomain(userRepository.findAll());
    }

    @Override
    public void saveUser(User user, String password) {
        UserEntity userEntity = userMapper.domaineToEntity(user);
        userEntity.setPassword(authentificationService.hashPassword(password));
        userRepository.save(userEntity);
    }
    @Override
    public User deleteUserByEmail(String email) {
        UserEntity userEntity = userRepository.getUserByEmail(email);
        userRepository.deleteById(userEntity.getId());
        return userMapper.entityToDomain(userEntity);
    }

    @Override
    public boolean authenticateUser(String username, String password) {
        UserEntity userEntity = userRepository.getUserByEmail(username);
        return authentificationService.verifyPassword(password, userEntity.getPassword());
    }

}
