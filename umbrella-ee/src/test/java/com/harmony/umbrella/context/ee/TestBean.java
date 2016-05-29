package com.harmony.umbrella.context.ee;

import java.lang.reflect.Field;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.AnnotationUtils;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "TestBean")
public class TestBean implements TestRemote, TestLocal {

    @EJB(lookup = "TestBean")
    private TestRemote testLogic;

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

    public static void main(String[] args) throws Exception {
        Field field = TestBean.class.getDeclaredField("testLogic");
        Map<String, Object> map = AnnotationUtils.toMap(field.getAnnotation(EJB.class));
        System.out.println(map);
    }

}
