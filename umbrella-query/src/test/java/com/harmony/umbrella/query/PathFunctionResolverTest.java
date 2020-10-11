package com.harmony.umbrella.query;

public class PathFunctionResolverTest {

    public static void main(String[] args) {
        PathFunctionResolver resolver = new PathFunctionResolver();
        Path<User> path = resolver.resolve(User::getName);
        System.out.println(path);
    }

}
