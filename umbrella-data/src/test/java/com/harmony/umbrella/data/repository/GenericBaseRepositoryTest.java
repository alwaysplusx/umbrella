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
package com.harmony.umbrella.data.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class GenericBaseRepositoryTest {

	@Autowired
	private BaseRepository<?> baseRepository;

	@Test
	public void testSaveS() {
		baseRepository.save(new User("wuxii"));
	}

	@Test
	public void testSaveIterableOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteIterableOfQ() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteClassOfSID() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllClassOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllClassOfSIterableOfID() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindAllClassOfSSpecificationOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneClassOfSID() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindOneClassOfSSpecificationOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testExistsClassOfQID() {
		fail("Not yet implemented");
	}

	@Test
	public void testExistsClassOfSSpecificationOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountClassOfQ() {
		fail("Not yet implemented");
	}

	@Test
	public void testCountClassOfSSpecificationOfS() {
		fail("Not yet implemented");
	}

}
