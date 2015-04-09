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
package com.harmony.modules.data;

import static org.junit.Assert.*;

import java.util.Collection;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.modules.data.persistence.User;
import com.harmony.modules.data.repository.UserRepository;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class SpringTestTest {

    @Resource
    private UserRepository userRepository;
    @PersistenceContext(unitName = "moon")
    private EntityManager em;

    @Test
    public void test() {
        assertNotNull(userRepository);
        userRepository.save(new User("wuxii"));
    }

    @Test
    public void testGetOne() {
        User user = userRepository.findOne(1l);
        System.out.println(user);
    }

    @Test
    public void testFindOneBy() {
        User user = userRepository.findOneByUserId(1l);
        // User user = userRepository.findOneByUsername("wuxii");
        System.out.println(user);
    }

    @Test
    public void testFindListBy() {
        Collection<User> users = userRepository.findListByUsername("wuxii");
        System.out.println(users);
    }

    @Test
    public void testExtractQueryString() {
        PersistenceProvider provider = PersistenceProvider.HIBERNATE;
        assertTrue(provider.canExtractQuery());
        Query query = em.createQuery("select o from User o");
        query.getResultList();
        String queryString = provider.extractQueryString(query);
        System.err.println(queryString);
    }

}
