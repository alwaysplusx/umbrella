package com.harmony.umbrella.data.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author wuxii@foxmail.com
 */
public class PersistenceProviderTest {

    public static void main(String[] args) {
        // StaticLogger.level = 100;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("umbrella");
        System.out.println(emf);

        // PersistenceXml.init();
        // System.out.println(PersistenceXml.persistenceUnits);
    }

}
