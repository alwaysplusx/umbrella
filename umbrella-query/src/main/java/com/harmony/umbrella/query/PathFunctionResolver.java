package com.harmony.umbrella.query;

import com.harmony.umbrella.query.support.SerializedLambda;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PathFunctionResolver {

    private Map<String, WeakReference<SerializedLambda>> cachedMap = new ConcurrentHashMap<>();

    public <T> Path<T> resolve(PathFunction<T, ?> pathFunction) {
        SerializedLambda lambda = getSerializedLambda(pathFunction);
        return (Path<T>) buildPath(lambda);
    }

    protected <T> Path<T> buildPath(SerializedLambda lambda) {
        String domainClassName = lambda.getImplClass().replace("/", ".");
        Class domainClass = null;
        String columnName = null;
        try {
            domainClass = Class.forName(domainClassName, true, Thread.currentThread().getContextClassLoader());
            columnName = toColumnName(lambda.getImplMethodName());
        } catch (Exception e) {
            throw new IllegalArgumentException("unresolved lambda " + lambda);
        }
        return new SimplePath<T>(columnName, domainClass);
    }

    private String toColumnName(String methodName) {
        String columnName = methodName;
        if (columnName.startsWith("get")) {
            columnName = methodName.substring(3);
        } else if (columnName.startsWith("is")) {
            columnName = methodName.substring(2);
        }
        return Character.toLowerCase(columnName.charAt(0)) + columnName.substring(1);
    }

    private SerializedLambda getSerializedLambda(PathFunction<?, ?> pathFunction) {
        String pathFunctionName = pathFunction.getClass().getName();
        return Optional.ofNullable(cachedMap.get(pathFunctionName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = this.toSerializedLambda(pathFunction);
                    cachedMap.put(pathFunctionName, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    private SerializedLambda toSerializedLambda(PathFunction<?, ?> pathFunction) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject(pathFunction);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    Class<?> clazz = super.resolveClass(desc);
                    return clazz == java.lang.invoke.SerializedLambda.class
                            ? SerializedLambda.class
                            : clazz;
                }
            };
            return (SerializedLambda) ois.readObject();
        } catch (Exception e) {
            throw new IllegalArgumentException("unknown path function " + pathFunction, e);
        }
    }

    private static class SimplePath<T> implements Path<T> {

        private String name;
        private Class<T> domainClass;

        private SimplePath(String name, Class<T> domainClass) {
            this.name = name;
            this.domainClass = domainClass;
        }

        @Override
        public @NotNull String getColumn() {
            return name;
        }

        @Override
        public @Nullable Class<T> getDomainClass() {
            return domainClass;
        }

        @Override
        public String toString() {
            return (domainClass != null ? domainClass.getName() : "unknown") + "#" + name;
        }

    }

}
