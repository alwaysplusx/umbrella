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
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.io.ResourceManager;
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
public abstract class XmlBeanMapper<T> implements NodeVisitor {

    private static final Converter[] allStringConverters;

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
        allStringConverters = cvs.toArray(new Converter[cvs.size()]);
    }

    private boolean root = true;
    protected String rootPath;

    protected Class<T> mapperType;
    protected T result;

    public XmlBeanMapper() {
    }

    public XmlBeanMapper(Class<T> mapperType) {
        this.mapperType = mapperType;
    }

    @Override
    public void visitElement(String path, Element element) {
        if (root) {
            root = false;
            rootPath = path;
            result = instanceBean();
            return;
        }
        setTargetValue(path.replace("/", ".").substring(rootPath.length() + 1), element);
    }

    protected abstract void setTargetValue(String path, Element element);

    public T getResult() {
        return result;
    }

    protected Class<?> getFieldType(Object target, String tagName) {
        return ReflectionUtils.findField(target.getClass(), tagName).getType();
    }

    protected Object getPathTarget(String path) {
        Object target = getResult();
        StringTokenizer st = new StringTokenizer(path, ".");
        while (st.hasMoreTokens()) {
            target = ReflectionUtils.getFieldValue(st.nextToken(), target);
        }
        return target;
    }

    public Converter getCustomeConvert(Element element, Class<?> requireType) {
        String convertName = element.getAttribute("convert");
        if (StringUtils.isNotBlank(convertName)) {
            try {
                return (Converter) ReflectionUtils.instantiateClass(convertName);
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            }
        } else if (requireType == String.class) {
            return null;
        } else {
            for (Converter converter : allStringConverters) {
                Class<? extends Converter> clazz = converter.getClass();
                if (requireType.isAssignableFrom(GenericUtils.getTargetGeneric(clazz, Converter.class, 1))) {
                    return converter;
                }
            }
        }
        throw new IllegalStateException("no suitable converter for " + requireType.getName());
    }

    protected T instanceBean() {
        return ReflectionUtils.instantiateClass(mapperType);
    }

}