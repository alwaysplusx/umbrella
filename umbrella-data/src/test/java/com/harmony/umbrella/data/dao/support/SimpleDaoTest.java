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
package com.harmony.umbrella.data.dao.support;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class SimpleDaoTest {

	@Autowired
	private Dao simpleDao;

	@Test
	public void testSimpleDao() {
		assertNotNull(simpleDao);
	}

	@Test
	public void testCRUD() {

		Iterable<User> users = simpleDao.save(Arrays.asList(new User("wuxii-a"), new User("wuxii-b")));

		for (User user : users) {
			assertNotNull(user.getUserId());
			user.setUsername("wuxii-1");
		}

		users = simpleDao.update(users);

		for (User user : users) {
			assertEquals("wuxii-1", user.getUsername());
		}

		simpleDao.delete(users);

		simpleDao.deleteAll(User.class);

		assertEquals(0, simpleDao.countAll(User.class));

	}

	@Test
	public void testFind() {

		simpleDao.findOne(User.class, 1l);

		simpleDao.findAll(User.class);

	}

	@Test(expected = NonUniqueResultException.class)
	public void testFindOne() {

		simpleDao.deleteAll(User.class);

		simpleDao.save(Arrays.asList(new User("wuxii-a"), new User("wuxii-b")));

		simpleDao.findOne("select o from User o where o.username=?1", "wuxii-a");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", "wuxii-b");

		simpleDao.findOne("select o from User o where o.username=:username", map);

		simpleDao.findOne("select o from User o");

	}

	@Test
	public void testFindAllAndCount() {

		simpleDao.deleteAll(User.class);

		simpleDao.save(Arrays.asList(new User("wuxii-a"), new User("wuxii-b"), new User("wuxii-c"), new User("wuxii-d"), new User("wuxii-e")));

		assertEquals(5, simpleDao.countAll(User.class));

		assertEquals(5, simpleDao.findAll(User.class).size());

		assertEquals(1, simpleDao.findAllBySQL("select o.* from t_user o where o.username=?1", User.class, "wuxii-a").size());

	}
}
