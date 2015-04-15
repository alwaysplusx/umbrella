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
package com.harmony.umbrella.mapper.dozer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import com.harmony.umbrella.mapper.Bar;
import com.harmony.umbrella.mapper.Baz;
import com.harmony.umbrella.mapper.Foo;

/**
 * @author wuxii@foxmail.com
 */
public class DozerTest {

	@Test
	public void testCopy() {
		DozerBeanMapper mapper = new DozerBeanMapper();
		Foo src = new Foo("wuxii", 25);
		src.setBirthday(Calendar.getInstance());
		Bar dest = new Bar("a", "100");

		mapper.map(src, dest);
		System.out.println(dest);
	}

	@Test
	public void testMappingFileCopy() {
		List<String> list = new ArrayList<String>();
		list.add("mapping.xml");
		DozerBeanMapper mapper = new DozerBeanMapper(list);
		Bar bar = new Bar("wuxii", "25");
		bar.setBirthday(new Date());
		Baz baz = new Baz();
		baz.setA("a");
		mapper.map(bar, baz);
		System.out.println(baz);
	}

}
