package com.harmony.umbrella.util;

import java.lang.reflect.Modifier;

/**
 * 类过滤
 *
 * @author wuxii@foxmail.com
 */
public interface ClassFilter {

    /**
     * 过滤类的信息
     *
     * @param clazz
     * @return
     */
    boolean accept(Class<?> clazz);

    public enum ClassFilterFeature implements ClassFilter {

        NOTNULL {
            @Override
            public boolean accept(Class<?> clazz) {
                return clazz != null;
            }

        },
        NEWABLE {
            @Override
            public boolean accept(Class<?> clazz) {
                if (!NOTNULL.accept(clazz) //
                        || INTERFACE.accept(clazz)//
                        || ABSTRACT.accept(clazz)//
                        || PRIVATE.accept(clazz) //
                        || PROTECTED.accept(clazz)) {
                    return false;
                }
                try {
                    return clazz.getDeclaredConstructor() != null;
                } catch (Exception e) {
                    return false;
                }
            }
        },
        ABSTRACT {
            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                return Modifier.isAbstract(clazz.getModifiers());
            }
        },
        INTERFACE {
            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                return clazz.isInterface();
            }
        },
        PUBLIC {
            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                return Modifier.isPublic(clazz.getModifiers());
            }
        },
        PROTECTED {
            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                return Modifier.isProtected(clazz.getModifiers());
            }
        },
        PRIVATE {
            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                return Modifier.isPrivate(clazz.getModifiers());
            }
        };

    }
}