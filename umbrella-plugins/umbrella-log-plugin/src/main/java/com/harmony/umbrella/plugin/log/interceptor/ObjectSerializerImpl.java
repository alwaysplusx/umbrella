package com.harmony.umbrella.plugin.log.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.harmony.umbrella.json.Json;
import com.harmony.umbrella.plugin.log.interceptor.LoggingInterceptor.ObjectSerializer;
import com.harmony.umbrella.plugin.log.util.ClassSortUtils;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.PathMatcher;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ObjectSerializerImpl implements ObjectSerializer {

    private final Set<Class<? extends Annotation>> ignoreTypes = new HashSet<Class<? extends Annotation>>();

    private final List<SerializeFilter> filters = new ArrayList<SerializeFilter>();

    private final List<SerializerFeature> features = new ArrayList<SerializerFeature>();

    private final Set<String> primitivePackages = new HashSet<String>();

    @SuppressWarnings("rawtypes")
    private final List<Class> endTypes = new ArrayList<Class>();

    private PathMatcher pathMatcher = new AntPathMatcher(".");

    public ObjectSerializerImpl() {
    }

    @SuppressWarnings("unchecked")
    protected ObjectSerializerImpl(Class<? extends Annotation>... ignoreTypes) {
        this.addIgnoreType(ignoreTypes);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object serialize(Object val) {
        if (val == null) {
            return Collections.emptyMap();
        }

        if (isPrimitiveType(val.getClass())) {
            return val;
        }

        Map result = new HashMap();
        Field[] fields = getAllFields(val.getClass());
        for (Field field : fields) {
            Object v = ReflectionUtils.getFieldValue(field, val);
            result.put(field.getName(), (v == null) ? null : serializeObject(v));
        }
        return result;
    }

    protected boolean isPrimitiveType(Class<?> clazz) {
        String pack = clazz.getPackage().getName();
        if (primitivePackages.contains(pack)) {
            return true;
        }
        for (String pp : primitivePackages) {
            if (pathMatcher.match(pp, pack)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    protected Field[] getAllFields(Class<?> targetClass) {
        if (targetClass == null) {
            return new Field[0];
        }
        Class end = getEndType(targetClass);
        Map<String, Field> holder = new LinkedHashMap<String, Field>();
        findAllFields(targetClass, end, holder);
        return holder.values().toArray(new Field[0]);
    }

    private void findAllFields(Class<?> current, Class<?> end, Map<String, Field> holder) {
        if (current == null || current == Object.class) {
            return;
        }
        Field[] fields = current.getDeclaredFields();
        for (Field f : fields) {
            String name = f.getName();
            if (!holder.containsKey(name) && !isIgnoreField(f)) {
                holder.put(name, f);
            }
        }
        if (current == end) {
            return;
        }
        findAllFields(current.getSuperclass(), end, holder);
    }

    private boolean isIgnoreField(Field field) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
            return true;
        }
        for (Class<? extends Annotation> annCls : ignoreTypes) {
            if (field.getAnnotation(annCls) != null) {
                return true;
            }
        }
        return false;
    }

    protected Object serializeObject(Object obj) {
        String text = Json.toJson(obj, filters.toArray(new SerializeFilter[0]), features.toArray(new SerializerFeature[0]));
        return Json.parse(text, Object.class);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<?> getEndType(Class<?> type) {
        ClassSortUtils.sort(endTypes);
        for (Class t : endTypes) {
            if (t.isAssignableFrom(type)) {
                return t;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void addIgnoreType(Class<? extends Annotation>... annCls) {
        for (Class<? extends Annotation> ac : annCls) {
            if (!ignoreTypes.contains(ac)) {
                this.ignoreTypes.add(ac);
            }
        }
    }

    public Set<Class<? extends Annotation>> getIgnoreTypes() {
        return ignoreTypes;
    }

    public void setIgnoreTypes(Collection<Class<? extends Annotation>> ignoreTypes) {
        this.ignoreTypes.clear();
        this.ignoreTypes.addAll(ignoreTypes);
    }

    public void addEndTypes(Collection<Class<?>> endTypes) {
        this.endTypes.addAll(endTypes);
    }

    public void addEndTypes(Class<?>... endTypes) {
        Collections.addAll(this.endTypes, endTypes);
    }

    public List<SerializeFilter> getSerializeFilters() {
        return filters;
    }

    public void setSerializeFilters(Collection<SerializeFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }

    public List<SerializerFeature> getSerializerFeatures() {
        return features;
    }

    public void setSerializerFeatures(Collection<SerializerFeature> features) {
        this.features.clear();
        this.features.addAll(features);
    }

    @SuppressWarnings("rawtypes")
    public List<Class> getEndTypes() {
        return endTypes;
    }

    @SuppressWarnings("rawtypes")
    public void setEndTypes(Collection<Class> endTypes) {
        this.endTypes.clear();
        this.endTypes.addAll(endTypes);
    }

    public Set<String> getPrimitivePackages() {
        return primitivePackages;
    }

    public void addPrimitivePackages(String... primitivePackages) {
        Collections.addAll(this.primitivePackages, primitivePackages);
    }

    public void setPrimitivePackages(Collection<String> primitivePackages) {
        this.primitivePackages.clear();
        this.primitivePackages.addAll(primitivePackages);
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

}
