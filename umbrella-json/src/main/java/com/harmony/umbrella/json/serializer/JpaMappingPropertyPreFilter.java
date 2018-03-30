package com.harmony.umbrella.json.serializer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JpaMappingPropertyPreFilter implements PropertyPreFilter {

    /**
     * 需要被懒加载处理的注解
     */
    static final List<Class<? extends Annotation>> MAPPING_ANNOTATIONS;

    static {
        List<Class<? extends Annotation>> temp = new ArrayList<>();
        temp.add(ManyToOne.class);
        temp.add(ManyToMany.class);
        temp.add(OneToMany.class);
        temp.add(OneToOne.class);
        MAPPING_ANNOTATIONS = Collections.unmodifiableList(temp);
    }

    /**
     * 是否尝试通过get方法去加载lazy的属性
     */
    private boolean tryFetch;

    public JpaMappingPropertyPreFilter() {
    }

    public JpaMappingPropertyPreFilter(boolean tryFetch) {
        this.tryFetch = tryFetch;
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (object == null) {
            return false;
        }
        Class<? extends Object> targetClass = object.getClass();
        Member member = getObjectMember(targetClass, name);
        FetchType fetchType = getFetchType(member);
        return fetchType == null || FetchType.EAGER.equals(fetchType) || (tryFetch && tryFetch(member, object));
    }

    protected Member getObjectMember(Class<?> targetClass, String name) {
        try {
            return MemberUtils.findMember(targetClass, name);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean tryFetch(Member member, Object object) {
        try {
            Object v = member.get(object);
            if (v == null) {
                return true;
            } else if (v instanceof Collection) {
                ((Collection) v).size();
            } else {
                return tryFirstReadMethod(member.getType(), v);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean tryFirstReadMethod(Class<?> clazz, Object object) throws Exception {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) //
                    && Modifier.isPublic(method.getModifiers())//
                    && !Modifier.isStatic(method.getModifiers())//
                    && MemberUtils.isReadMethod(method)) {
                ReflectionUtils.invokeMethod(method, object);
                return true;
            }
        }
        return false;
    }

    public FetchType getFetchType(Member member) {
        Annotation ann = null;
        for (Class<? extends Annotation> annCls : MAPPING_ANNOTATIONS) {
            ann = member.getAnnotation(annCls);
            if (ann != null) {
                break;
            }
        }
        return ann == null ? null : (FetchType) AnnotationUtils.getValue(ann, "fetch");
    }

}
