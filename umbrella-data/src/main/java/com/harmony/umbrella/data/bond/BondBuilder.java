package com.harmony.umbrella.data.bond;

import static com.harmony.umbrella.data.bond.Bond.Link.*;

import java.util.Arrays;
import java.util.Collection;

import com.harmony.umbrella.data.bond.Bond.Link;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Sort.Direction;

public class BondBuilder {

    public static BondBuilder newInstance() {
        return new BondBuilder();
    }

    public Bond equal(String name, Object value) {
        beforeBondCreate(name, value, EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, EQUAL));
    }

    public Bond notEqual(String name, Object value) {
        beforeBondCreate(name, value, NOT_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, NOT_EQUAL));
    }

    public Bond in(String name, Collection<?> c) {
        beforeBondCreate(name, c, IN, false);
        return afterBondCreated(new InBond(name, c));
    }

    public Bond in(String name, Object... values) {
        beforeBondCreate(name, values, IN, false);
        return afterBondCreated(new InBond(name, Arrays.asList(values)));
    }

    public Bond notIn(String name, Collection<?> c) {
        beforeBondCreate(name, c, NOT_IN, false);
        return afterBondCreated(new InBond(name, c, NOT_IN));
    }

    public Bond notIn(String name, Object... values) {
        beforeBondCreate(name, values, NOT_IN, false);
        return afterBondCreated(new InBond(name, Arrays.asList(values), NOT_IN));
    }

    public Bond isNull(String name) {
        beforeBondCreate(name, null, NULL, false);
        return afterBondCreated(new NullBond(name));
    }

    public Bond isNotNull(String name) {
        beforeBondCreate(name, null, NOT_NULL, false);
        return afterBondCreated(new NullBond(name, NOT_NULL));
    }

    public Bond like(String name, String value) {
        beforeBondCreate(name, value, LIKE, false);
        return afterBondCreated(new ComparisonBond(name, value, LIKE));
    }

    public Bond notLike(String name, String value) {
        beforeBondCreate(name, value, NOT_LIKE, false);
        return afterBondCreated(new ComparisonBond(name, value, NOT_LIKE));
    }

    public Bond ge(String name, Object value) {
        beforeBondCreate(name, value, GREATER_THAN_OR_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, GREATER_THAN_OR_EQUAL));
    }

    public Bond gt(String name, Object value) {
        beforeBondCreate(name, value, GREATER_THAN, false);
        return afterBondCreated(new ComparisonBond(name, value, GREATER_THAN));
    }

    public Bond le(String name, Object value) {
        beforeBondCreate(name, value, LESS_THAN_OR_EQUAL, false);
        return afterBondCreated(new ComparisonBond(name, value, LESS_THAN_OR_EQUAL));
    }

    public Bond lt(String name, Object value) {
        beforeBondCreate(name, value, LESS_THAN, false);
        return afterBondCreated(new ComparisonBond(name, value, LESS_THAN));
    }

    public Bond inline(String name, String expression, Link link) {
        beforeBondCreate(name, expression, link, true);
        return afterBondCreated(new ComparisonBond(name, expression, link, true));
    }

    protected void beforeBondCreate(String name, Object value, Link link, boolean isInline) {

    }

    protected AbstractBond afterBondCreated(AbstractBond bond) {
        return bond;
    }

    public Sort asc(String... name) {
        return new Sort(Direction.ASC, name);
    }

    public Sort desc(String... name) {
        return new Sort(Direction.DESC, name);
    }

    public Bond and(Bond... bonds) {
        return new DisjunctionBond(Arrays.asList(bonds));
    }

    public Bond or(Bond... bonds) {
        return new ConjunctionBond(Arrays.asList(bonds));
    }

    // public Bond conjunction() {
    // return new Conjunction(AND, new ArrayList<Bond>());
    // }
    //
    // public Bond disjunction() {
    // return new Disjunction(OR, new ArrayList<Bond>());
    // }

}
