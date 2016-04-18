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

    public static final String ROOT = "$";

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
        return setTargetFieldValue(result, toFieldPath(path), element);
    }

    protected abstract boolean setTargetFieldValue(T target, String fieldPath, Element element);

    /**
     * 映射的结果
     * 
     * @return
     */
    public T getResult() {
        return result;
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

    protected String toFieldPath(String path) {
        return path.substring(rootPath.length() + 1).replace(XmlUtil.PATH_SPLIT, ".");
        // return ROOT + "." + path.substring(rootPath.length() + 1).replace(XmlUtil.PATH_SPLIT, ".");
    }

    /**
     * 初始化映射的对象
     * 
     * @param element
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