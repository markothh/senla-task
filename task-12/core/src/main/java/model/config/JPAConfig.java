package model.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JPAConfig {
    private static final Logger logger = LogManager.getLogger();
    private static final EntityManagerFactory emf = buildEMF();

    private static final String INIT_MSG = "Фабрика EntityManager инициализирована";

    public static EntityManager getEntityManager() {
        logger.info(INIT_MSG);
        return emf.createEntityManager();
    }

    private static EntityManagerFactory buildEMF() {
        return Persistence.createEntityManagerFactory("persistenceUnit");
    }
}
