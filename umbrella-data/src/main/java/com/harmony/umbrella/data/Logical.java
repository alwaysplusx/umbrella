package com.harmony.umbrella.data;

/**
 * @author wuxii@foxmail.com
 */
public enum Logical {

    AND("and"), OR("or");

    private String name;

    private Logical(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static boolean isAnd(Logical logical) {
        return AND.equals(logical);
    }

    public static boolean isOr(Logical logical) {
        return OR.equals(logical);
    }

}
