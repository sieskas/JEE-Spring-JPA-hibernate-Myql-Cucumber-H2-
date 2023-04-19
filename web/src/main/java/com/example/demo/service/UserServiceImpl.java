package com.example.demo.service;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.UserEntity;

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
    @Inject
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
