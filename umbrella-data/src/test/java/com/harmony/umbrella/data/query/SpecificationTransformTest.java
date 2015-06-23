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
import com.harmony.umbrella.data.bond.Bonds;
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
    private static final SpecificationTransform st = SpecificationTransform.getInstance();

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

    public static void main(String[] args) {
        Bond bond0 = builder.equal("username", "wuxii");
        Bond bond1 = builder.equal("userId", 12l);
        Bond bond2 = builder.equal("username", "a");
        Bond bond3 = builder.equal("userId", 12l);

        Bond bond4 = builder.or(bond0, bond1);

        Bond bond5 = builder.or(bond2, bond3);

        Bond bond = builder.and(bond4, bond5);

        System.out.println(st.toSQL("t_user", bond));
        System.out.println(st.toXQL("User", bond).getXQL());
        System.out.println(st.toXQL("User", bond0, bond1, bond2, bond3).getXQL());
        System.out.println(st.toXQL("User", Bonds.or(bond0, bond1, bond2, bond3)).getXQL());

    }

}
