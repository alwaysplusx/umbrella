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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;
import org.junit.Test;

import com.harmony.umbrella.mapper.vo.deep.Dest;
import com.harmony.umbrella.mapper.vo.deep.Src;

/**
 * @author wuxii@foxmail.com
 */
public class DozerMapperTest {

	@Test(expected = MappingException.class)
	public void testDynamicAddMapping() throws Exception {
		DozerBeanMapper mapper = new DozerBeanMapper();
		mapper.map(new Src("wuxii"), Dest.class);
		// 不能动态添加
		mapper.addMapping(new FileInputStream(new File("src/test/resources/mapping.xml")));
	}

	@Test
	public void testMapper() {
		DozerBeanMapper mapper = new DozerBeanMapper(Arrays.asList("mapping.xml"));
		B b = mapper.map(new A("a"), B.class);
		assertNotNull(b);
		assertNotNull(b.name);
	}

	public static class A {
		private String name;

		public A() {
		}

		public A(String name) {
			this.name = name;
		}

		 public String getName() {
		 return name;
		 }
		
		 public void setName(String name) {
		 this.name = name;
		 }

		@Override
		public String toString() {
			return getClass().getName() + ":{\"name\":\"" + name + "\"}";
		}
	}

	public static class B {
		private String name;

		public B() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public B(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return getClass().getName() + ":{\"name\":\"" + name + "\"}";
		}
	}

}
