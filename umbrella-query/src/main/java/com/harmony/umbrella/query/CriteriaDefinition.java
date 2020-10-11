package com.harmony.umbrella.query;

public interface CriteriaDefinition {

    enum Combinator {
        INITIAL, AND, OR;
    }

    enum Comparator {

        EQ("="),
        NEQ("!="),
        LT("<"),
        LTE("<="),
        GT(">"),
        GTE(">="),
        IS_NULL("IS NULL"),
        IS_NOT_NULL("IS NOT NULL"),
        LIKE("LIKE"),
        NOT_LIKE("NOT LIKE"),
        NOT_IN("NOT IN"),
        IN("IN"),
        IS_TRUE("IS TRUE"),
        IS_FALSE("IS FALSE");

        private final String comparator;

        Comparator(String comparator) {
            this.comparator = comparator;
        }

        public String getComparator() {
            return comparator;
        }

    }

}
