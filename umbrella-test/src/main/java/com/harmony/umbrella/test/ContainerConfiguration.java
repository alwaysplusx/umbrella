package com.harmony.umbrella.test;

import javax.naming.spi.InitialContextFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wuxii@foxmail.com
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainerConfiguration {

    public Class<? extends InitialContextFactory> initialContextFactory() default InitialContextFactory.class;

    public String providerUrl() default "";

    public String location() default "";

    Property[] properties() default {};

}
