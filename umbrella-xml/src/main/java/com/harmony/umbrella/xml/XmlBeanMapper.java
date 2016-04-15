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
package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.GenericUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public abstract class XmlBeanMapper<T> extends ElementAcceptor {

    /**
     * 所有支持string转化的converter
     */
    private static final List<Converter> allStringConverters;

    static {
        final List<Converter> cvs = new ArrayList<Converter>();
        ResourceManager.getInstance().getClasses(ApplicationContext.APPLICATION_PACKAGE, new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                if (Converter.class.isAssignableFrom(clazz) //
                        && ClassFilterFeature.NEWABLE.accept(clazz)//
                        && GenericUtils.getTargetGeneric(clazz, Converter.class, 0) == String.class) {
                    cvs.add((Converter) ReflectionUtils.instantiateClass(clazz));
                }
                return false;
            }
        });
        allStringConverters = cvs;
    }

    // 标志位
    private boolean root = true;
    protected String rootPath;

    private Class<T> mappedType;
    protected T result;

    public XmlBeanMapper() {
    }

    public XmlBeanMapper(Class<T> mappedType) {
        this.mappedType = mappedType;
    }

    @Override
    public boolean acceptElement(String path, Element element) {
        if (root) {
            root = false;
            rootPath = path;
            result = instanceBean();
            return true;
        }
        String fieldPath = path.replace(XmlUtil.PATH_SPLIT, ".");
        setTargetValue(fieldPath.substring(rootPath.length() + 1), element);
        return true;
    }

    /**
     * 根据element的中的属性name来获取字段的名称, 如果不存在对应的name属性则取对应的tagName
     * 
     * @param element
     * @return
     */
    protected String getFieldName(Element element) {
        String fieldName = element.getAttribute("name");
        return StringUtils.isBlank(fieldName) ? element.getTagName() : fieldName;
    }

    /**
     * 给指定的字段设置值
     * 
     * 给User设置值的路径, a.nameA 指向User对象内的A中nameA字段
     * 
     * <pre>
     * class A {
     *     String nameA;
     * }
     * 
     * class B {
     *     String nameB;
     * }
     * 
     * class User {
     *     A a;
     *     B b;
     * }
     * </pre>
     * 
     * @param path
     *            映射的结果对象的字段的路径
     * @param element
     *            对应的xml element
     */
    protected abstract void setTargetValue(String path, Element element);

    /**
     * 映射的结果
     * 
     * @return
     */
    public T getResult() {
        return result;
    }

    /**
     * 根据字段路径找寻字段对应的对象
     * 
     * @param path
     *            字段路径
     * @return 字段路径对应的值
     */
    protected Object getTarget(String path) {
        Object target = getResult();
        if (StringUtils.isNotBlank(path)) {
            for (String token : path.split("\\.")) {
                target = ReflectionUtils.getFieldValue(token, target);
            }
        }
        return target;
    }

    /**
     * 根据目标类型加载对应的string converter
     * 
     * @param requireType
     *            映射的目标类型
     * @return
     */
    public Converter getConvert(Class<?> requireType) {
        // 将元数据类型转为封装类型
        if (requireType.isPrimitive()) {
            requireType = ClassUtils.getPrimitiveWrapperType(requireType);
        }
        for (Converter converter : allStringConverters) {
            Class<? extends Converter> clazz = converter.getClass();
            if (requireType.isAssignableFrom(GenericUtils.getTargetGeneric(clazz, Converter.class, 1))) {
                return converter;
            }
        }
        return null;
    }

    /**
     * 初始化映射的对象
     */
    protected T instanceBean() {
        return ReflectionUtils.instantiateClass(getMappedType());
    }

    @SuppressWarnings("unchecked")
    protected Class<T> getMappedType() {
        if (this.mappedType == null) {
            this.mappedType = (Class<T>) GenericUtils.getTargetGeneric(getClass(), XmlBeanMapper.class, 0);
        }
        return this.mappedType;
    }

}