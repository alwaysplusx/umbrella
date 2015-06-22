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
package com.harmony.umbrella.data.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.bond.Bond;
import com.harmony.umbrella.data.bond.BondBuilder;
import com.harmony.umbrella.data.bond.QBond;
import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.persistence.User;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class SpecificationTransformTest {

    private static final BondBuilder builder = new BondBuilder();
    private static final SpecificationTransform st = new SpecificationTransform();

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private Dao simpleDao;

    @Test
    public void test() {
        Bond bond = builder.and(builder.in("userId", 1l));
        QBond qbond = st.toXQL("User", bond);
        List<User> result = simpleDao.findAll(qbond.getXQL(), qbond.getParams());
        System.out.println(result);
    }

}
