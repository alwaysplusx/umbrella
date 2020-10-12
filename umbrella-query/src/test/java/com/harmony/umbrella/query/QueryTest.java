package com.harmony.umbrella.query;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import static com.harmony.umbrella.query.jpa.JpaQueryBuilder.from;

public class QueryTest {

    private static EntityManager entityManager = Persistence
            .createEntityManagerFactory("umbrella")
            .createEntityManager();

    public static void main(String[] args) {
        PathFunction<User, String> name = User::getName;
        from(User.class)
                .withEntityManager(entityManager)
                .where(e ->
                        e.equal(name, "david")
                                .or()
                                .equal(name, "mary")
                )
                .orderBy(e -> e.desc(User::getName))
                .execute()
                .getSingleResult()
                .ifPresent(System.out::println);
    }

}
