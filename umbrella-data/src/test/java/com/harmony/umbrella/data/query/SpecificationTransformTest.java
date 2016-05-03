package com.harmony.umbrella.data.query;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.Dao;
import com.harmony.umbrella.data.QBond;
import com.harmony.umbrella.data.bond.BondBuilder;
import com.harmony.umbrella.data.bond.Bonds;
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
        QBond qbond = st.toQBond("User", bond);
        List<User> result = simpleDao.findAll(qbond.getQuery(), qbond.getParams());
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
        System.out.println(st.toQBond("User", bond).getQuery());
        System.out.println(st.toQBond("User", bond0, bond1, bond2, bond3).getQuery());
        System.out.println(st.toQBond("User", Bonds.or(bond0, bond1, bond2, bond3)).getQuery());

    }
}
