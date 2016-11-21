package com.harmony.umbrella.plugin.log.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import com.harmony.umbrella.json.SerializerConfig;
import com.harmony.umbrella.log.annotation.LogIgnore;
import com.harmony.umbrella.plugin.log.interceptor.AbstractLoggingInterceptor.ObjectSerializer;

/**
 * @author wuxii@foxmail.com
 */
public class ObjectSerializerFactory {

    @SuppressWarnings("unchecked")
    public static ObjectSerializer createDefault() {
        ObjectSerializerImpl serializer = new ObjectSerializerImpl();
        serializer.addIgnoreType(LogIgnore.class, EJB.class, Inject.class, PersistenceUnit.class, PersistenceContext.class);
        serializer.addPrimitivePackages("javax.**", "java.**", "com.sun.**");
        return serializer;
    }

    public static ObjectSerializer create(LogSerializerConfig cfg) {
        ObjectSerializerImpl serializer = new ObjectSerializerImpl();
        serializer.setEndTypes(cfg.endTypes);
        serializer.setIgnoreTypes(cfg.ignoreType);
        serializer.setPrimitivePackages(cfg.primitivePackages);
        if (cfg.cfg != null) {
            serializer.setSerializeFilters(Arrays.asList(cfg.cfg.getFilters()));
            serializer.setSerializerFeatures(Arrays.asList(cfg.cfg.getFeatures()));
        }
        return serializer;
    }

    public static class LogSerializerConfig {

        @SuppressWarnings("rawtypes")
        private List<Class> endTypes = new ArrayList<Class>();

        private Set<String> primitivePackages = new HashSet<String>();

        private Set<Class<? extends Annotation>> ignoreType = new HashSet<Class<? extends Annotation>>();

        @SuppressWarnings("rawtypes")
        private SerializerConfig cfg;

        public LogSerializerConfig withEndType(Class<?>... endTypes) {
            Collections.addAll(this.endTypes, endTypes);
            return this;
        }

        public LogSerializerConfig withPrimitivePackage(String... pack) {
            Collections.addAll(this.primitivePackages, pack);
            return this;
        }

        @SuppressWarnings("unchecked")
        public LogSerializerConfig withIgnoreType(Class<? extends Annotation>... annCls) {
            Collections.addAll(this.ignoreType, annCls);
            return this;
        }

        @SuppressWarnings("rawtypes")
        public LogSerializerConfig withSerializerConfig(SerializerConfig cfg) {
            this.cfg = cfg;
            return this;
        }

    }

}
