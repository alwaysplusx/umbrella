package com.harmony.umbrella.validator.util;

import org.junit.Test;

import com.harmony.umbrella.validator.Foo;
import com.harmony.umbrella.validator.Validators;

/**
 * @author wuxii@foxmail.com
 */
public class ValidatorUtilsTest {

    @Test
    public void testGetViolationMessage() {
        Foo foo = new Foo(null, 110l);
        foo.setBirthday("abc");
        String message = Validators.getViolationMessage(foo);
        System.out.println(message);
    }

}
