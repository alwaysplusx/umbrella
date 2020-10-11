package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.User;
import org.springframework.data.jpa.domain.Specification;

public class JpaQueryBuilderTest {

    public static void main(String[] args) {
        Specification<User> spec = new JpaCriteriaBuilder<User>()
                .equal(User::getName, "david")
                .get();
        JpaQueryBuilder
                .from(User.class)
                .where(spec);
    }

}
