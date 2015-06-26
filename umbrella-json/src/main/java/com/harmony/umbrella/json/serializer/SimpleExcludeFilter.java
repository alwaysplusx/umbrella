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
package com.harmony.umbrella.json.serializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerialContext;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleExcludeFilter implements PropertyPreFilter {

    private static final Logger log = LoggerFactory.getLogger(SimpleExcludeFilter.class);

    private final Class<?> clazz;
    private final Set<String> excludes = new HashSet<String>();

    private boolean arrayCheck = false;
    
    public SimpleExcludeFilter(String... excludes) {
        this(null, excludes);
    }

    public SimpleExcludeFilter(Class<?> clazz, String... properties) {
        super();
        this.clazz = clazz;
        for (String item : properties) {
            if (item != null) {
                this.excludes.add(item);
            }
        }
    }

    @Override
    public boolean apply(JSONSerializer serializer, Object source, String name) {
        if (source == null) {
            return true;
        }
        if (clazz != null && !clazz.isInstance(source)) {
            return true;
        }

        String fieldContextName = getContextNameWithArrayIndex(serializer.getContext(), name);
        log.info(fieldContextName);
        
        for (String exclude : excludes) {
            if (fieldContextName.startsWith(exclude)) {
                return false;
            }
        }

        return true;
    }

    
    protected String getContextNameWithArrayIndex(SerialContext context, String name) {
        StringBuilder buf = new StringBuilder();
        SerialContext currentContext = context;

        // 往上迭代直到父上下文为空
        while (currentContext.getParent() != null) {

            String fieldName;
            Object currentFieldName = currentContext.getFieldName();

            SerialContext parentContext = currentContext.getParent();
            Object parent = parentContext.getObject();

            if ((parent instanceof Collection || parent.getClass().isArray()) 
                    && currentFieldName instanceof Number) {
                fieldName = "[" + currentFieldName + "]";
            } else {
                fieldName = String.valueOf(currentFieldName);
            }

            if (buf.indexOf("[") == 0) {
                buf.insert(0, fieldName);
            } else {
                buf.insert(0, fieldName).insert(fieldName.length(), '.');
            }
            
            // reset sctx
            currentContext = parentContext;
        }

        return buf.append(name).toString();
    }

    protected String getContextName(SerialContext context, String name) {
        StringBuilder buf = new StringBuilder();
        SerialContext currentContext = context;
        
        // 往上迭代直到父上下文为空
        while (currentContext.getParent() != null) {
            
            Object currentFieldName = currentContext.getFieldName();
            String fieldName = String.valueOf(currentFieldName);
            
            SerialContext parentContext = currentContext.getParent();
            Object parent = parentContext.getObject();
            
            if (!((parent instanceof Collection || parent.getClass().isArray()) 
                    && currentFieldName instanceof Number)) {
                buf.insert(0, fieldName).insert(fieldName.length(), '.');
            }
            
            // reset sctx
            currentContext = parentContext;
        }
        
        return buf.append(name).toString();
    }
    
    public boolean isEnableArrayCheck() {
        return arrayCheck;
    }
    
    public void setArrayCheck(boolean arrayCheck) {
        this.arrayCheck = arrayCheck;
    }
    
}