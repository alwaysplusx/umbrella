package com.harmony.umbrella.message.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * {@linkplain Session#createConsumer(javax.jms.Destination, String) Session.createConsomer(dest, selector)}, the second
 * parameter. only messages with properties matching the message selector expression are delivered. A value of null or
 * an empty string indicates that there is no message selector for the message consumer.
 * 
 * @author wuxii@foxmail.com
 * @see Session
 * @see MessageConsumer
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MessageSelector {

    public String value() default "";

    public Class<?> type() default Void.class;

}
