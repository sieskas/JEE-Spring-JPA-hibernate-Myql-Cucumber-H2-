//package com.example.demo.repository.todo;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//import org.hibernate.query.Query;
//import org.reflections.Reflections;
//import org.springframework.stereotype.Component;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Named;
//import javax.persistence.Entity;
//import java.io.Serializable;
//import java.util.List;
//import java.util.Set;
//
//@Component
//public class  DaoProvider {
//
//    private final SessionFactory factory;
//
//    public DaoProvider() {
//
//        Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
//        for(Class<?> clazz : getConfiguration()) {
//            configuration.addAnnotatedClass(clazz);
//        }
//        factory = configuration.buildSessionFactory();
//    }
//
//    public <T> List<T> getQueryToList(String query, Class<T> tClass) {
//        Session currentSession = factory.getSessionFactory().getCurrentSession();
//        currentSession.beginTransaction();
//        Query<T> nativeQuery = currentSession.createNativeQuery(query, tClass);
//        List<T> nativeQueryResultList = nativeQuery.getResultList();
//        currentSession.getTransaction().commit();
//        return nativeQueryResultList;
//    }
//
//    public <T> T getQueryToSingleResult(String query, Class<T> tClass, Object... args) {
//        Session currentSession = factory.getSessionFactory().getCurrentSession();
//        currentSession.beginTransaction();
//        T uniqueResult = createAndConfigureQuery(currentSession, query, tClass, args).uniqueResult();
//        currentSession.getTransaction().commit();
//        return uniqueResult;
//    }
//
//    public <T> int executeUpdate(String query, Class<T> tClass, Object... args) {
//        Session currentSession = factory.getSessionFactory().getCurrentSession();
//        currentSession.beginTransaction();
//        int uniqueResult = createAndConfigureQuery(currentSession, query, tClass, args).executeUpdate();
//        currentSession.getTransaction().commit();
//        return uniqueResult;
//    }
//
//    private <T> Query<T> createAndConfigureQuery(Session currentSession, String query, Class<T> tClass, Object... args ) {
//        Query<T> nativeQuery = currentSession.createNativeQuery(query, tClass);
//        for (int i = 0; i < args.length; i++) {
//            nativeQuery.setParameter("param" + i, args[i]);
//        }
//        return nativeQuery;
//    }
//
//    public Set<Class<?>> getConfiguration() {
//        Reflections reflections = new Reflections("com.example.demo.repository.entity");
//        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Entity.class);
//        return classes;
//    }
//
//
//    public Integer saveEntity(Object entity) {
//        Session currentSession = factory.getSessionFactory().getCurrentSession();
//        currentSession.beginTransaction();
//        Serializable entityId = currentSession.save(entity);
//        currentSession.flush();
//        currentSession.getTransaction().commit();
//        return (Integer) entityId;
//    }
//
//}
