package com.example.demo.repository;

import com.example.demo.repository.entity.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.reflections.Reflections;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Named
@ApplicationScoped
public class  DaoProvider {

    private final SessionFactory factory;

    public DaoProvider() {

        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
        for(Class<?> clazz : getConfiguration()) {
            configuration.addAnnotatedClass(clazz);
        }
        factory = configuration.buildSessionFactory();
    }

    public <T> List<T> getQueryToList(String query, Class<T> tClass) {
        Session currentSession = factory.getSessionFactory().getCurrentSession();
        currentSession.beginTransaction();
        Query<T> nativeQuery = currentSession.createNativeQuery(query, tClass);
        List<T> nativeQueryResultList = nativeQuery.getResultList();
        currentSession.getTransaction().commit();
        return nativeQueryResultList;
    }

    public <T> T getQueryToSingleResult(String query, Class<T> tClass) {
        Session currentSession = factory.getSessionFactory().getCurrentSession();
        currentSession.beginTransaction();
        Query<T> nativeQuery = currentSession.createNativeQuery(query, tClass);
        T uniqueResult = nativeQuery.uniqueResult();
        currentSession.getTransaction().commit();
        return uniqueResult;
    }


    public Set<Class<?>> getConfiguration() {
        Reflections reflections = new Reflections("com.example.demo.repository.entity");
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);
        return classes;
    }


    public Integer saveEntity(Object entity) {
        Session currentSession = factory.getSessionFactory().getCurrentSession();
        currentSession.beginTransaction();
        Serializable entityId = currentSession.save(entity);
        currentSession.flush();
        currentSession.getTransaction().commit();
        return (Integer) entityId;
    }

}
