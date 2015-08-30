/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.monitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.annotation.InternalProperty;
import com.harmony.umbrella.monitor.annotation.Mode;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 监控基础抽象类
 * 
 * @param <T>
 *            监控的资源类型，由子类指定
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMonitor<T> implements Monitor<T> {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractMonitor.class);

    /**
     * 受到监控的模版名单
     */
    protected final Set<String> patternList = new CopyOnWriteArraySet<String>();

    /**
     * 资源名单，综合{@link #getPolicy()}定性资源名单的意义
     */
    protected final Set<T> resourceList = new CopyOnWriteArraySet<T>();

    /**
     * 监控策略
     */
    protected MonitorPolicy policy = MonitorPolicy.WhiteList;

    /**
     * 创建资源匹配器
     * 
     * @param pattern
     *            通过模版路径创建资源匹配工具
     * @return 模版资源匹配工具
     */
    protected abstract ResourceMatcher<T> getResourceMatcher();

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
    }

    @Override
    public void setPolicy(MonitorPolicy policy) {
        Assert.notNull(policy, "cant' t set null to policy");
        this.policy = policy;
    }

    public Set<T> getResources() {
        return resourceList;
    }

    @Override
    public Set<String> getPatterns() {
        return patternList;
    }

    @Override
    public boolean isMonitored(T resource) {
        switch (policy) {
        case Skip:
            return false;
        case All:
            return true;
        case WhiteList:
            // resource 表示白名单， 排除白名单中的所有
            if (resourceList.contains(resource)) {
                return false;
            }
        case BlockList:
            // resource 表示黑名单， 监控黑名单中所有
            if (resourceList.contains(resource)) {
                return true;
            }
        default:
            for (String pattern : patternList) {
                if (getResourceMatcher().match(pattern, resource)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removePattern(String pattern) {
        patternList.remove(pattern);
    }

    public void addPattern(String pattern) {
        Assert.notBlank(pattern, "can't monitor blank or null pattern");
        patternList.add(pattern);
    }

    public void removeResource(T resource) {
        resourceList.remove(resource);
    }

    public void addResource(T resource) {
        Assert.notNull(resource, "can't monitor null resource");
        resourceList.add(resource);
    }

    public void cleanAll() {
        patternList.clear();
        resourceList.clear();
    }

    public Map<String, Object> attackMethodProperty(Object target, Method method, Mode mode) {
        InternalProperty[] properties = getMonitorProperty(method, InternalProperty.class);
        return attackProperty(target, properties, mode);
    }

    public Map<String, Object> attackClassProperty(Object target, Mode mode) {
        InternalProperty[] properties = getMonitorProperty(target.getClass(), InternalProperty.class);
        return attackProperty(target, properties, mode);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Object> attackProperty(Object target, InternalProperty[] properties, Mode mode) {
        Map<String, Object> result = null;
        if (properties != null && properties.length > 0) {
            result = new HashMap<String, Object>();
            for (InternalProperty internalProperty : properties) {
                if (mode.inRange(internalProperty.mode())) {
                    Attacker attacker = getAttacker(internalProperty.attacker());
                    result.putAll(attacker.attack(target, internalProperty.properties()));
                }
            }
        }
        return result == null ? Collections.<String, Object> emptyMap() : result;
    }

    /**
     * 获取method上的{@linkplain Monitored}注解，并获取注解内对于的属性(传入的propertyType)
     * 
     * @param method
     *            过滤的方法
     * @param propertyType
     *            要获取的属性值
     * @return 对于propertyType的属性
     */
    @SuppressWarnings("unchecked")
    protected final <E extends Annotation> E[] getMonitorProperty(Method method, Class<E> propertyType) {
        Monitored ann = method.getAnnotation(Monitored.class);
        return (E[]) (ann == null ? null : propertyType == InternalProperty.class ? ann.internalProperties() : ann.httpProperties());
    }

    @SuppressWarnings("unchecked")
    protected final <E extends Annotation> E[] getMonitorProperty(Class<?> clazz, Class<E> propertyType) {
        Monitored ann = clazz.getAnnotation(Monitored.class);
        return (E[]) (ann == null ? null : propertyType == InternalProperty.class ? ann.internalProperties() : ann.httpProperties());
    }

    protected Attacker<?> getAttacker(Class<? extends Attacker<?>> attackerClass) {
        return ReflectionUtils.instantiateClass(attackerClass);
    }
}
