package com.example.demo.service;

import com.example.demo.app.v1.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private List<UserEntity> userEntities;
    private List<User> users;

    @BeforeEach
    public void setUp() {
        userEntities = new ArrayList<>();
        userEntities.add(new UserEntity(/* add constructor arguments if needed */));
        userEntities.add(new UserEntity(/* add constructor arguments if needed */));

        users = new ArrayList<>();
        users.add(User.builder().build());
        users.add(User.builder().build());
    }

    @Test
    void testGetUsers() {
        when(userRepository.findAll()).thenReturn(userEntities);
        when(userMapper.entityToDomain(userEntities)).thenReturn(users);

        List<User> result = userService.getUsers();

        assertEquals(users, result);
        verify(userRepository).findAll();
        verify(userMapper).entityToDomain(userEntities);
    }

    @Test
    void testSaveUser() {
        User user = User.builder().build();
        UserEntity userEntity = UserEntity.builder().build();
        when(userMapper.domaineToEntity(user)).thenReturn(userEntity);

        userService.saveUser(user);

        verify(userMapper).domaineToEntity(user);
        verify(userRepository).save(userEntity);
    }
}