package employee;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;

    public static void configure() {

        Configuration config = new Configuration();
        config.configure("hibernate.cfg.xml");

        Properties env = loadEnv();
        config.setProperty("hibernate.connection.url", env.getProperty("DB_URL"));
        config.setProperty("hibernate.connection.username", env.getProperty("DB_USERNAME"));
        config.setProperty("hibernate.connection.password", env.getProperty("DB_PASSWORD"));

        config.addAnnotatedClass(EmployeePojo.class);


        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();

        sessionFactory = config.buildSessionFactory(serviceRegistryObj);
    }

    private static Properties loadEnv() {
        Properties env = new Properties();
        try (FileInputStream in = new FileInputStream(".env")) {
            env.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Could not read .env (expected in the working directory, next to pom.xml)", e);
        }
        return env;
    }

    public void createSession(){
        if (session != null && session.isOpen()) {
            return;
        }
        session = sessionFactory.openSession();
    }

    public void closeSession(){
        if (session != null) {
            session.close();
        }
    }
    public Session getSession() {
        return session;
    }
    public void beginTransaction() {
        transaction = session.beginTransaction();
    }
    public void commitTransaction() {
        if (transaction != null) {
            transaction.commit();
        }
    }
}
