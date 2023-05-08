//package com.example.demo.repository;
//
//import com.example.demo.repository.entity.UserEntity;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//import java.util.List;
//
//@Repository
//public class UserRepositoryImpl implements UserRepository {
//
//    @Autowired
//    private DaoProvider daoProvider;
//
//    @Override
//    public List<UserEntity> findAll() {
//        return daoProvider.getQueryToList("SELECT * FROM users", UserEntity.class);
//    }
//
//    @Override
//    public Integer save(UserEntity userEntity) {
//        return daoProvider.saveEntity(userEntity);
//    }
//
//    @Override
//    public UserEntity getUserByEmail(String email) {
//        return daoProvider.getQueryToSingleResult("SELECT * FROM users where email = :param0", UserEntity.class, email);
//    }
//
//    @Override
//    public void deleteById(int id) {
//        daoProvider.executeUpdate("DELETE FROM users WHERE id = :param0", UserEntity.class, id);
//    }
//
//}
