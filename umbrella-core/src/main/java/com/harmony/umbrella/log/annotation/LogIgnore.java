package com.harmony.umbrella.log.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author wuxii@foxmail.com
 */
@Target({ METHOD, FIELD, TYPE })
@Retention(RUNTIME)
public @interface LogIgnore {

}
