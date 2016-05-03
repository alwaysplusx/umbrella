package com.harmony.umbrella.examples.jpa;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author wuxii@foxmail.com
 */
public class JapSetUpTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("umbrella");
        EntityManager em = emf.createEntityManager();
        Object result = em.createNativeQuery("select 1 from dual").getSingleResult();
        assertEquals(1l, result);
    }

}
