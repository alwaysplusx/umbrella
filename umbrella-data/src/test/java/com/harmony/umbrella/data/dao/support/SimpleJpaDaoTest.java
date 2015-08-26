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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class SimpleJpaDaoTest {

	@Autowired
	private JpaDao<User, Long> userJpaDao;

	@Test
	public void testJpaDao() {
		assertNotNull(userJpaDao);
	}

	@Test
	public void testDeleteAllInBatch() {
		Iterable<User> users = userJpaDao.save(Arrays.asList(new User("wuxii-a"), new User("wuxii-b"), new User("wuxii-c"), new User("wuxii-d")));
		userJpaDao.deleteInBatch(users);
	}

}
