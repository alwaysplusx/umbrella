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
package com.harmony.umbrella.validator.validators;

import java.text.ParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.validator.constraints.FormatDate;

/**
 * @author wuxii@foxmail.com
 */
public class FormatDateValidator implements ConstraintValidator<FormatDate, String> {

    private Formats.NullableDateFormat ndf;

    @Override
    public void initialize(FormatDate annotation) {
        this.ndf = Formats.createDateFormat(annotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        try {
            ndf.parseDate(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
