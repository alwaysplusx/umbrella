package com.harmony.umbrella.data.jpa;

/**
 * @author wuxii@foxmail.com
 */
public class PersistenceProviderTest {

    public static void main(String[] args) {
        //        EntityManagerFactory emf = Persistence.createEntityManagerFactory("umbrella");
        //        System.out.println(emf);

        PersistenceXml.init();
        System.out.println(PersistenceXml.persistenceUnits);
    }

}
