package com.harmony.umbrella.example.entity;

/**
 * @author wuxii@foxmail.com
 */
public class Bar {

    private String name;

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
