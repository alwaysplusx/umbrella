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
package com.harmony.umbrella.data.meta;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Metamodel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.persistence.Group;
import com.harmony.umbrella.data.persistence.Group.GroupPk;
import com.harmony.umbrella.data.persistence.User;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.data.query.JpaEntityInformation;
import com.harmony.umbrella.data.query.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class MetaTest {

    @PersistenceContext(unitName = "moon")
    private EntityManager em;

    // @Test
    // public void testEmbeddableId() {
    // Metamodel metamodel = em.getMetamodel();
    // Person person = new Person();
    // person.setAddress("");
    // person.setPk(new PersonPk(1l, ""));
    // EntityInformation<Person, PersonPk> info = new
    // JpaEntityInformation<Person, PersonPk>(Person.class, metamodel);
    // show(info, person);
    // }

    @Test
    public void testNormal() {
        Metamodel metamodel = em.getMetamodel();
        User user = new User();
        user.setUserId(1l);
        user.setUsername("wuxii");
        EntityInformation<User, Long> info = new JpaEntityInformation<User, Long>(User.class, metamodel);
        show(info, user);
    }

    @Test
    public void testIdClass() {
        Metamodel metamodel = em.getMetamodel();
        Group group = new Group();
        group.setId(1l);
        group.setName("wuxii");
        group.setAddress("a");
        EntityInformation<Group, GroupPk> info = new JpaEntityInformation<Group, GroupPk>(Group.class, metamodel);
        show(info, group);
    }

    public <T, ID extends Serializable> void show(EntityInformation<T, ID> info, T entity) {
        System.err.println(info.getEntityName());
        System.err.println(info.getId(entity));
        System.err.println(info.getIdAttribute());
        System.err.println(info.getIdAttributeNames());
        System.err.println(info.getIdType());
        System.err.println(info.getJavaType());
        System.err.println(info.getTableName());
        System.err.println(info.isNew(entity));
        System.out.println(info.getId(entity));
    }

    @Test
    public void testToExpressionRecursively() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        StringTokenizer tokenizer = new StringTokenizer("person.address", ".");
        Expression<?> exp = QueryUtils.toExpressionRecursively(root, tokenizer);
        System.out.println(exp);
    }

}
