package com.harmony.umbrella.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.FetchType;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.data.Persistable;
import com.harmony.umbrella.json.serializer.MemberFilterFilter;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LazyAttributeFilter extends MemberFilterFilter {

    private static final Log log = Logs.getLog(LazyAttributeFilter.class);

    /**
     * 是否尝试通过get方法去加载lazy的属性
     */
    private boolean tryFetch;
    /**
     * 需要被过滤的lazy注解
     */
    private final Set<Class<? extends Annotation>> anns = new HashSet<Class<? extends Annotation>>();

    public LazyAttributeFilter(Class<? extends Annotation>... anns) {
        this(false, anns);
    }

    public LazyAttributeFilter(boolean tryFetch, Class<? extends Annotation>... anns) {
        this.tryFetch = tryFetch;
        this.addFilterAnnotationClass(anns);
    }

    public LazyAttributeFilter() {
        this.allowNull = false;
    }

    @Override
    protected boolean accept(Member member, Object target) {
        FetchType fetchType = getFetchType(member);
        return fetchType == null || FetchType.EAGER.equals(fetchType) || (tryFetch && tryFetch(member, target));
    }

    @SuppressWarnings("rawtypes")
    public boolean tryFetch(Member member, Object object) {
        try {
            Object v = member.get(object);
            if (v == null) {
                return true;
            } else if (v instanceof Collection) {
                ((Collection) v).size();
            } else if (v instanceof Persistable) {
                ((Persistable) v).getId();
            } else {
                tryFirstReadMethod(member.getType(), v);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void tryFirstReadMethod(Class<?> clazz, Object object) throws Exception {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) //
                    && Modifier.isPublic(method.getModifiers())//
                    && !Modifier.isStatic(method.getModifiers())//
                    && ReflectionUtils.isReadMethod(method)) {
                ReflectionUtils.invokeMethod(method, object);
                return;
            }
        }
    }

    public FetchType getFetchType(Member member) {
        Annotation ann = null;
        for (Class<? extends Annotation> annCls : anns) {
            ann = member.getAnnotation(annCls);
            if (ann != null) {
                break;
            }
        }
        return ann == null ? null : (FetchType) AnnotationUtils.getAnnotationValue(ann, "fetch");
    }

    public void addFilterAnnotationClass(Class<? extends Annotation>... annCls) {
        for (Class<? extends Annotation> cls : annCls) {
            try {
                if (cls.getMethod("fetch") != null) {
                    this.anns.add(cls);
                }
            } catch (Exception e) {
                log.error("{} annotation not have fetch attribute", cls, e);
            }
        }
    }
}
