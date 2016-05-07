package com.harmony.umbrella.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.harmony.umbrella.excel.CellResolver;

/**
 * @author wuxii@foxmail.com
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    int value() default -1;

    @SuppressWarnings("rawtypes")
    Class<? extends CellResolver> cellResolver() default CellResolver.class;

    String header() default "";

}
