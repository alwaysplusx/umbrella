/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.mapper.converter;

import java.util.Calendar;
import java.util.Date;

import org.dozer.CustomConverter;

import com.harmony.umbrella.util.DateFormatUtils;

/**
 * @author wuxii@foxmail.com
 */
public class DateToStringConverter implements CustomConverter {

	@Override
	public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
		if (sourceFieldValue instanceof Date) {
			return DateFormatUtils.DEFAULT_DATEFORMAT.format((Date) sourceFieldValue);
		} else if (sourceFieldValue instanceof Calendar) {
			return DateFormatUtils.DEFAULT_DATEFORMAT.format((Calendar) sourceFieldValue);
		}
		return null;
	}

}
