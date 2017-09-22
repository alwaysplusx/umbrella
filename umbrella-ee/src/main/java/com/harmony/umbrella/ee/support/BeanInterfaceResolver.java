package com.harmony.umbrella.ee.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.ClassUtils;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.util.EJBUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanInterfaceResolver implements PartResolver<Class> {

    private static final Log log = Logs.getLog(BeanInterfaceResolver.class);

    private Set<String> remoteClassSuffixes;

    private Set<String> localClassSuffixes;

    /*
     * 以下条件返回对应的结果
     * 
     * beanClass是remote接口
     * beanClass对应的remoteClasses存在
     * 
     * 通过local接口猜测remote接口
     *  1. 移除local接口的后缀
     *  2. 添加remote接口的后缀
     *  3. 检查拼接后的className是否存在
     *  4. 判断存在的class是否是remote接口
     */
    @Override
    public Set<Class> resolve(BeanDefinition bd) {
        Set<Class> result = new HashSet<Class>();

        Class<?> beanClass = bd.getBeanClass();
        if (EJBUtils.isRemoteClass(beanClass) && !EJBUtils.isLocalClass(beanClass)) {
            result.add(beanClass);
            return result;
        }

        Class<?>[] remoteClasses = bd.getRemoteClasses();
        if (remoteClasses != null && remoteClasses.length > 0) {
            Collections.addAll(result, remoteClasses);
            return result;
        }

        Class<?>[] localClasses = bd.getLocalClasses();
        if (localClasses != null && localClasses.length > 0) {
            Collection<Class> guessResult = guessRemoteClasses(localClasses);
            if (guessResult != null) {
                result.addAll(guessResult);
            }
        }
        log.debug("{} resolve bean interface as {}", beanClass, result);
        return result;
    }

    protected Collection<Class> guessRemoteClasses(final Class<?>[] localClasses) {
        if (remoteClassSuffixes == null || localClassSuffixes == null//
                || remoteClassSuffixes.isEmpty() || localClassSuffixes.isEmpty()) {
            return Arrays.asList();
        }
        Set<Class> result = new HashSet<Class>();
        for (Class<?> local : localClasses) {
            final String localFullName = local.getName();
            final String localName = local.getSimpleName();
            final String localPrefix = localFullName.substring(0, localFullName.length() - localName.length());

            String localNameRemoveSuffix = null;

            for (String localSuffix : localClassSuffixes) {
                if (StringUtils.isNotBlank(localSuffix) && localName.endsWith(localSuffix)) {
                    localNameRemoveSuffix = localName.substring(0, localName.length() - localSuffix.length());
                    break;
                }
            }
            if (localNameRemoveSuffix == null) {
                continue;
            }

            for (String remoteSuffix : remoteClassSuffixes) {
                String remoteFullName = localPrefix + localNameRemoveSuffix + remoteSuffix;
                try {
                    Class<?> cls = ClassUtils.forName(remoteFullName, ClassUtils.getDefaultClassLoader());
                    if (EJBUtils.isRemoteClass(cls)) {
                        result.add(cls);
                    } else {
                        log.info("{} class is not remote class", cls);
                    }
                } catch (Error e) {
                    log.error(e);
                } catch (ClassNotFoundException e) {
                    log.debug("remote class {} not found", remoteFullName);
                } catch (Exception e) {
                    log.warn(e);
                }
            }
        }
        return result;
    }

    public Set<String> getRemoteClassSuffixes() {
        return remoteClassSuffixes;
    }

    public void setRemoteClassSuffixes(Set<String> remoteClassSuffixes) {
        this.remoteClassSuffixes = remoteClassSuffixes;
    }

    public Set<String> getLocalClassSuffixes() {
        return localClassSuffixes;
    }

    public void setLocalClassSuffixes(Set<String> localClassSuffixes) {
        this.localClassSuffixes = localClassSuffixes;
    }

}
