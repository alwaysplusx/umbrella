package com.harmony.umbrella.example.entity;

/**
 * @author wuxii@foxmail.com
 */
public class Foo {

    private String name;

    public Foo() {
    }

    public Foo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{name:");
        builder.append(name);
        builder.append("}");
        return builder.toString();
    }
}
