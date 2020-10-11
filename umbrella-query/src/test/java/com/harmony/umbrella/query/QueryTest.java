package com.harmony.umbrella.query;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import static com.harmony.umbrella.query.jpa.JpaQueryBuilder.from;

public class QueryTest {

    private static EntityManager entityManager = Persistence
            .createEntityManagerFactory("umbrella")
            .createEntityManager();

    public static void main(String[] args) {
        from(User.class)
                .withEntityManager(entityManager)
                .where(e ->
                        e.equal(User::getName, "david")
                                .or()
                                .equal(User::getName, "mary")
                )
                .execute()
                .getSingleResult()
                .ifPresent(System.out::println);
    }

}
