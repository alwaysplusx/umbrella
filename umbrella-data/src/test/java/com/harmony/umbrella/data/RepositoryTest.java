package com.harmony.umbrella.data;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.harmony.umbrella.data.entity.Model;

/**
 * @author wuxii@foxmail.com
 */
public class RepositoryTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();
    static SimpleJpaRepository<Model, Long> repository = new SimpleJpaRepository<Model, Long>(Model.class, entityManager);

    @BeforeClass
    public static void beforeClass() {
        entityManager.getTransaction().begin();
        entityManager.persist(new Model(1l, "wuxii", "code1"));
        entityManager.persist(new Model(2l, "david", "code2"));
        entityManager.getTransaction().commit();
    }

    @Test
    public void testFindAll() {
        Page<Model> result = repository.findAll(new PageRequest(1, 20));
        System.out.println(result);
        for (Model model : result) {
            System.out.println(model);
        }
    }

}
