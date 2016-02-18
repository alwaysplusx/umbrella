package com.harmony.umbrella.log.annotation;

import com.harmony.umbrella.log.Level;

public @interface LogSys {

    String module() default "";

    String action() default "";

    Level level() default Level.INFO;

}