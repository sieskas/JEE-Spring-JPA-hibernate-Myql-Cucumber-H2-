package com.example.demo.repository;

import com.example.demo.repository.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @InjectMocks
    private UserRepositoryImpl userRepository;

    @Mock
    private DaoProvider daoProvider;

    @Test
    void findAll_shouldReturnAllUsers() {
        List<UserEntity> expectedUsers = Arrays.asList(
                UserEntity.builder().build(), UserEntity.builder().build()
        );
        when(daoProvider.getQueryToList("SELECT * FROM users", UserEntity.class)).thenReturn(expectedUsers);

        List<UserEntity> actualUsers = userRepository.findAll();

        assertEquals(expectedUsers, actualUsers);
    }


    @Test
    void save_shouldReturnUserId() {
        UserEntity userToSave = UserEntity.builder().build();
        Integer expectedUserId = 3;
        when(daoProvider.saveEntity(userToSave)).thenReturn(expectedUserId);

        Integer actualUserId = userRepository.save(userToSave);

        assertEquals(expectedUserId, actualUserId);
    }
}