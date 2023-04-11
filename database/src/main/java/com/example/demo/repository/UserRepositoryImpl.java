package com.example.demo.repository;

import com.example.demo.repository.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
@ApplicationScoped
public class UserRepositoryImpl implements UserRepository {

    @Inject
    private DaoProvider daoProvider;

    @Override
    public List<UserEntity> findAll() {
        return daoProvider.getQueryToList("SELECT * FROM users", UserEntity.class);
    }

    @Override
    public Integer save(UserEntity userEntity) {
        return daoProvider.saveEntity(userEntity);
    }

}
