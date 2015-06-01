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
package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;

import org.junit.Test;

import com.harmony.umbrella.util.Formats.NullableNumberFormat;

/**
 * @author wuxii@foxmail.com
 */
public class FormatsTest {

	@Test
	public void testNullableNumberFormat() throws ParseException {

		BigDecimal value = new BigDecimal("0.231");

		NullableNumberFormat nnf = Formats.createNumberFormat("#.##", RoundingMode.HALF_UP);

		BigDecimal v1 = nnf.bigDecimalValue(value);
		assertEquals(new BigDecimal("0.23"), v1);

		Double v2 = nnf.doubleValue(value);
		assertEquals(new Double(0.23), v2);

		String v3 = nnf.format(value);
		assertEquals("0.23", v3);

		Long v4 = nnf.longValue(value);
		assertEquals(Long.valueOf(0), v4);

		String source = "0.231";

		Number v5 = nnf.parse(source);
		assertEquals(0.23, v5);

		BigDecimal v6 = nnf.parseBigDecimal(source);
		assertEquals(new BigDecimal("0.23"), v6);

		Double v7 = nnf.parseDouble(source);
		assertEquals(new Double("0.23"), v7);

		Long v8 = nnf.parseLong(source);
		assertEquals(Long.valueOf(0), v8);

		assertEquals(Long.valueOf(1), Formats.longValue(0.53, RoundingMode.HALF_UP));

		assertEquals(Long.valueOf(1), Formats.longValue("0.53", RoundingMode.HALF_UP));

	}
}
