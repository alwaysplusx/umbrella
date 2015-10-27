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
package com.harmony.umbrella.validator.util;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.validator.ValidVisitor;

/**
 * 用于做一些简单的检验
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ValidatorUtils {

    // thread-safe
    private static Validator validator;

    /**
     * Returns an initialized {@link Validator} instance using the factory
     * defaults for message interpolator, traversable resolver and constraint
     * validator factory.
     * <p/>
     * Validator instances can be pooled and shared by the implementation.
     *
     * @return an initialized {@code Validator} instance
     * @see Validation#buildDefaultValidatorFactory()
     * @see ValidatorFactory#getValidator()
     */
    public static Validator getValidator() {
        if (validator == null) {
            validator = getValidatorFactory().getValidator();
        }
        return validator;
    }

    private static ValidatorFactory getValidatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    /**
     * 检查bean的违规信息
     * 
     * @param object
     * @return 所有的违规信息用逗号({@code ,})隔开,如果没有违规信息返回{@code null}
     */
    public static String getViolationMessage(Object object) {
        return getViolationMessage(object, Default.class);
    }

    /**
     * 检查bean的违规信息
     * 
     * @param object
     * @param groups
     * @return
     */
    public static String getViolationMessage(Object object, Class<?>... groups) {
        return getViolationMessage(object, null, groups);
    }

    public static String getViolationMessage(Object object, ValidVisitor visitor, Class<?>... groups) {
        StringBuilder buf = new StringBuilder();

        Iterator<ConstraintViolation<Object>> it = getValidator().validate(object, groups).iterator();
        while (it.hasNext()) {
            buf.append(it.next().getMessage());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        if (visitor != null) {
            String message = visitor.valid(object);
            if (!StringUtils.isBlank(message)) {
                buf.append(", ").append(message);
            }
        }

        return buf.length() == 0 ? null : buf.toString();
    }

    /**
     * 检查bean的违规信息条数
     * 
     * @param object
     * @return
     */
    public static int getNumberOfViolations(Object object) {
        return getValidator().validate(object).size();
    }

    /**
     * 检查bean的违规信息条数
     * 
     * @param object
     * @return
     */
    public static int getNumberOfViolations(Object object, Class<?>... groups) {
        return getValidator().validate(object, groups).size();
    }

    /**
     * 检查bean是否违规
     * 
     * @param object
     * @return
     */
    public static boolean isViolation(Object object, Class<?> groups) {
        return getNumberOfViolations(object, groups) > 0;
    }

}
