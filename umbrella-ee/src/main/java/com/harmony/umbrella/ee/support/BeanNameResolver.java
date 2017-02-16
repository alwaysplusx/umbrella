package com.harmony.umbrella.ee.support;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.util.EJBUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassFilter;
import com.harmony.umbrella.util.ClassFilterFeature;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanNameResolver implements PartResolver<String> {

    private static final Log log = Logs.getLog(BeanNameResolver.class);

    private Set<String> beanNameSuffixes;

    private Set<String> interfaceSuffixes;

    private List<Class<? extends Annotation>> sequance;

    private boolean guessAlways = true;

    private Map<String, String> beanNameMapping = new HashMap<String, String>();

    /*
     * 以下判定返回结果
     * beanClass是session bean, 返回注解对应的name/mappedName
     * beanClass非接口, 返回simpleName
     * 
     * beanClass是接口或者remote接口
     * 查找类路径下的接口的所有子类
     * 添加子类的session bean注解名称, 或者simpleName
     * +
     * 通过配置猜测对应的beanName
     * 
     */
    @SuppressWarnings({ "rawtypes" })
    @Override
    public Set<String> resolve(BeanDefinition bd) {
        Set<String> result = new HashSet<String>();

        Class<?> beanClass = bd.getBeanClass();

        String beanName = beanNameMapping.get(beanClass.getName());
        if (StringUtils.isNotBlank(beanName)) {
            result.add(beanName);
            return result;
        }

        if (EJBUtils.isSessionBean(beanClass)) {
            // 找到bean上的session注解的beanName
            String name = getBeanName(beanClass);
            if (StringUtils.isNotBlank(name)) {
                result.add(name);
                return result;
            }
        }

        if (result.isEmpty() && !beanClass.isInterface()) {
            result.add(beanClass.getSimpleName());
            return result;
        }

        // 查找子类
        if (beanClass.isInterface()) {
            Class[] subClasses = getSubClasses(beanClass);
            for (Class c : subClasses) {
                if (EJBUtils.isSessionBean(c)) {
                    String name = getBeanName(c);
                    if (StringUtils.isNotBlank(name)) {
                        result.add(name);
                    }
                }
            }
        }

        if (guessAlways || result.isEmpty()) {
            String[] guessResult = guessBeanName(beanClass);
            if (guessResult != null && guessResult.length > 0) {
                result.addAll(Arrays.asList(guessResult));
            }
        }
        log.debug("{} resolve bean name as {}", beanClass, result);
        return result;
    }

    /*
     * 通过bean definition猜想对应的bean name
     */
    protected String[] guessBeanName(final Class<?> beanClass) {
        if (beanNameSuffixes == null || interfaceSuffixes == null //
                || beanNameSuffixes.isEmpty() || interfaceSuffixes.isEmpty()) {
            return new String[0];
        }
        Set<String> result = new HashSet<String>();
        final String name = beanClass.getSimpleName();

        String nameRemoveSuffix = null;
        for (String interfaceSuffix : interfaceSuffixes) {
            if (StringUtils.isNotBlank(interfaceSuffix) && name.endsWith(interfaceSuffix)) {
                nameRemoveSuffix = name.substring(0, name.length() - interfaceSuffix.length());
                break;
            }
        }

        for (String beanNameSuffix : beanNameSuffixes) {
            String beanNameFullName = nameRemoveSuffix + beanNameSuffix;
            if (!result.contains(beanNameFullName)) {
                result.add(beanNameFullName);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    protected Class[] getSubClasses(Class<?> clazz) {
        return ApplicationContext.getApplicationClasses(new ClassFilter() {
            @Override
            public boolean accept(Class<?> c) {
                return c.isAssignableFrom(c) && c != clazz && ClassFilterFeature.NEWABLE.accept(c);
            }
        });
    }

    protected String getBeanName(Class<?> beanClass) {
        // 1. @Stateless#name 
        // 2. @Stateless#mappedName
        Class[] seq = sequance != null ? sequance.toArray(new Class[sequance.size()]) : new Class[0];
        String beanName = EJBUtils.getName(beanClass, seq);
        if (StringUtils.isBlank(beanName)) {
            beanName = EJBUtils.getMappedName(beanClass, seq);
        }
        return beanName;
    }

    public List<Class<? extends Annotation>> getSequance() {
        return sequance;
    }

    public void setSequance(List<Class<? extends Annotation>> sequance) {
        this.sequance = sequance;
    }

    public boolean isGuessAlways() {
        return guessAlways;
    }

    public void setGuessAlways(boolean guessAlways) {
        this.guessAlways = guessAlways;
    }

    public Map<String, String> getBeanNameMapping() {
        return beanNameMapping;
    }

    public void setBeanNameMapping(Map<String, String> beanNameMapping) {
        this.beanNameMapping = beanNameMapping;
    }

    public Set<String> getBeanNameSuffixes() {
        return beanNameSuffixes;
    }

    public void setBeanNameSuffixes(Set<String> beanNameSuffixes) {
        this.beanNameSuffixes = beanNameSuffixes;
    }

    public Set<String> getInterfaceSuffixes() {
        return interfaceSuffixes;
    }

    public void setInterfaceSuffixes(Set<String> interfaceSuffixes) {
        this.interfaceSuffixes = interfaceSuffixes;
    }

}
