package server.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil
{
    private static SessionFactory sessionFactory;

    static
    {
        try
        {
            Configuration configuration = new Configuration();
            configuration.configure(); // still load hibernate.cfg.xml
            configuration.addAnnotatedClass(server.models.Author.class);
            configuration.addAnnotatedClass(server.models.Book.class);
            configuration.addAnnotatedClass(server.models.Category.class);
            configuration.addAnnotatedClass(server.models.Status.class);
            configuration.addAnnotatedClass(server.models.Review.class);


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        } catch (Throwable ex)
        {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
}
