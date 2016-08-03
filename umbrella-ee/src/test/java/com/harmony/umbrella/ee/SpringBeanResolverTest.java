package com.harmony.umbrella.ee;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:default-ee.xml")
public class SpringBeanResolverTest {

    @Autowired
    private BeanResolver beanResolver;

    @Test
    public void testGuessName() {
        BeanDefinition bd = new BeanDefinition(FooLocal.class);
        String[] names = beanResolver.guessNames(bd);
        for (String name : names) {
            System.out.println(name);
        }
    }

}
