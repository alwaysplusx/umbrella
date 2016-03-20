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
package com.harmony.umbrella.log.util;

/**
 * @author wuxii@foxmail.com
 */
public class StackUtils {

    public static String fullyQualifiedClassName() {
        return fullyQualifiedClassName(StackUtils.class.getName(), 1);
    }

    public static String fullyQualifiedClassName(Class<?> clazz) {
        return fullyQualifiedClassName(clazz.getName(), 0);
    }

    public static String fullyQualifiedClassName(Class<?> clazz, int beforeIndex) {
        return fullyQualifiedClassName(clazz.getName(), beforeIndex);
    }

    public static String fullyQualifiedClassName(String className) {
        return fullyQualifiedClassName(className, 0);
    }

    public static String fullyQualifiedClassName(String className, int beforeIndex) {
        StackTraceElement ste = find(className, beforeIndex);
        return ste == null ? null : ste.toString();
    }

    /**
     * 在当前线程中查找对应类的stackTraceElement
     * 
     * @param className
     * @param reversal
     * @return
     */
    public static StackTraceElement find(String className, int beforeIndex) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            if (stackTrace[i].getClassName().equals(className)) {
                int index = i + beforeIndex;
                if (index > stackTrace.length - 1) {
                    return null;
                }
                return stackTrace[index];
            }
        }
        return null;
    }

}
