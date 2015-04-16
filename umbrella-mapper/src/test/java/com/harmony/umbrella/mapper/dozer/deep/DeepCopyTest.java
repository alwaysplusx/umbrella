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
package com.harmony.umbrella.mapper.dozer.deep;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.dozer.DozerBeanMapper;
import org.junit.Test;

import com.harmony.umbrella.mapper.vo.deep.Dest;
import com.harmony.umbrella.mapper.vo.deep.Src;
import com.harmony.umbrella.mapper.vo.deep.SubDest;
import com.harmony.umbrella.mapper.vo.deep.SubSubDest;

/**
 * @author wuxii@foxmail.com
 */
public class DeepCopyTest {

	@Test
	public void testDeepCopyNormal() {
		// 普通map无法深层复制
		DozerBeanMapper mapper = new DozerBeanMapper();
		Src src = new Src("wuxii");
		Dest dest = new Dest(new SubDest(new SubSubDest()));
		mapper.map(src, dest);
		assertNull(dest.getSubDest().getSubSubDest().getName());
		dest = mapper.map(src, Dest.class);
		assertNull(dest.getSubDest());
	}

	@Test
	public void testDeepCopyFromXMLFile() {
		DozerBeanMapper mapper = new DozerBeanMapper(Arrays.asList("mapping.xml"));
		Src src = new Src("wuxii");
		Dest dest = new Dest();
		mapper.map(src, dest, "deepCopy");
		assertNotNull(dest.getSubDest());
		assertNotNull(dest.getSubDest().getSubSubDest());
		assertNotNull(dest.getSubDest().getSubSubDest().getName());
		System.out.println("type one -> " + dest);
		dest = mapper.map(src, Dest.class, "deepCopy");
		assertNotNull(dest.getSubDest());
		assertNotNull(dest.getSubDest().getSubSubDest());
		assertNotNull(dest.getSubDest().getSubSubDest().getName());
		System.out.println("type second -> " + dest);
	}

}
