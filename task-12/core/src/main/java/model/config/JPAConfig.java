package model.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPAConfig {
    private static final Logger logger = LogManager.getLogger();
    private static final EntityManagerFactory entityManagerFactory = buildEntityManagerFactory();

    private static final String INIT_MSG = "Фабрика EntityManager инициализирована";

    public static EntityManager getEntityManager() {
        logger.info(INIT_MSG);
        return entityManagerFactory.createEntityManager();
    }

    private static EntityManagerFactory buildEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("persistenceUnit");
    }
}
