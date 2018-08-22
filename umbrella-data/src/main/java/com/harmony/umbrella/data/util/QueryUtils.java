package com.harmony.umbrella.data.util;

import org.springframework.util.Assert;

/**
 * 查询工具类
 */
public abstract class QueryUtils {

    private QueryUtils() {
    }

//    public static <T> Specification<T> all(Specification<T>... specs) {
//        Assert.notEmpty(specs, "spec not allow empty");
//        return (root, query, cb) -> {
//            Predicate predicate = null;
//            for (Specification<T> spec : specs) {
//                Predicate p = spec.toPredicate(root, query, cb);
//                if (p == null && !(spec instanceof NullableSpecification)) {
//                    throw new QueryException(spec + " specification not predicate");
//                }
//                if (p != null) {
//                    predicate = predicate == null ? p : cb.and(predicate, p);
//                }
//            }
//            return predicate;
//        };
//    }

    public static StringExpression parse(String name) {
        Assert.notNull(name, "expression not allow null");

        String function = null;
        String expression = name;

        int left = name.indexOf("(");
        int right = name.indexOf(")");
        if (left != -1 && right != -1) {
            function = name.substring(0, left).trim();
            expression = name.substring(left + 1, name.length() - 1).trim();
        }

        return new StringExpression(function, expression);
    }

}
