package dao;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class SimpleJPARepository<T, ID>
        implements JPARepository<T, ID> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    public SimpleJPARepository(
            SessionFactory sessionFactory,
            Class<T> entityClass) {

        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    @Override
    public void add(T entity) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            session.persist(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public void update(T entity) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            session.merge(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public void delete(T entity) {

        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {

            transaction = session.beginTransaction();

            session.remove(entity);

            transaction.commit();

        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }

            throw e;
        }
    }

    @Override
    public T findById(ID id) {

        try (Session session = sessionFactory.openSession()) {

            return session.find(entityClass, id);
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session
                    .createQuery(
                            "FROM " + entityClass.getSimpleName(),
                            entityClass
                    )
                    .getResultList();
        }

    }
}