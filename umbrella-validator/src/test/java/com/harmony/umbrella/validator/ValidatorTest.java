package com.harmony.umbrella.validator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ValidatorTest {

    public static void main(String[] args) throws Exception {
        Configuration<?> configure = Validation.byDefaultProvider()//
                .configure();
        ValidatorFactory validatorFactory = configure.buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        print(validator.validate(new Foo()));

    }

    @Test
    public void testAddMapping() throws Exception {

        Validator validator = Validation.byDefaultProvider().configure()//
                .addMapping(new FileInputStream(new File("src/test/resources/constraints-bar.xml")))//
                .buildValidatorFactory().getValidator();
        print(validator.validate(new Bar()));
        
    }

    private static void print(Set<?> set) {
        print(set.iterator());
    }

    private static void print(Iterator<?> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

}
