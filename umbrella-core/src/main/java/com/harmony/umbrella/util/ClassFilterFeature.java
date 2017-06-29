package com.harmony.umbrella.util;

import java.lang.reflect.Modifier;

public enum ClassFilterFeature implements ClassFilter {

    NOTNULL {
        @Override
        public boolean doAccept(Class<?> clazz) {
            return clazz != null;
        }

    },
    NEWABLE {
        @Override
        public boolean doAccept(Class<?> clazz) {
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
        public boolean doAccept(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            return Modifier.isAbstract(clazz.getModifiers());
        }
    },
    INTERFACE {
        @Override
        public boolean doAccept(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            return clazz.isInterface();
        }
    },
    PUBLIC {
        @Override
        public boolean doAccept(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            return Modifier.isPublic(clazz.getModifiers());
        }
    },
    PROTECTED {
        @Override
        public boolean doAccept(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            return Modifier.isProtected(clazz.getModifiers());
        }
    },
    PRIVATE {
        @Override
        public boolean doAccept(Class<?> clazz) {
            if (clazz == null) {
                return false;
            }
            return Modifier.isPrivate(clazz.getModifiers());
        }
    };

    @Override
    public final boolean accept(Class<?> clazz) {
        try {
            return doAccept(clazz);
        } catch (Throwable e) {
            // ignore
        }
        return false;
    }

    protected abstract boolean doAccept(Class<?> clazz);

    /**
     * 单抛出异常时候filter返回false
     * 
     * @param filter
     *            classFilter
     * @param clazz
     *            待校验的class
     * @return true accept
     */
    public static boolean safetyAccess(ClassFilter filter, Class<?> clazz) {
        try {
            return filter.accept(clazz);
        } catch (Throwable e) {
            // ignore
        }
        return false;
    }

}