package server.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import server.util.HibernateUtil;

import java.io.Serializable;
import java.util.List;

public class BaseDAO<T, ID extends Serializable> {

    private final Class<T> entityClass;

    public BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.save(entity);
        tx.commit();
        session.close();
    }

    public void update(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.update(entity);
        tx.commit();
        session.close();
    }

    public void delete(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.delete(entity);
        tx.commit();
        session.close();
    }

    public void deleteById(ID id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        T entity = session.get(entityClass, id);
        if (entity != null) {
            session.delete(entity);
        }
        tx.commit();
        session.close();
    }

    public T findById(ID id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        T entity = session.get(entityClass, id);
        session.close();
        return entity;
    }

    public List<T> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<T> list = session.createQuery("from " + entityClass.getSimpleName(), entityClass).list();
        session.close();
        return list;
    }
}

