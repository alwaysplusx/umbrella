package com.harmony.umbrella.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.harmony.umbrella.validator.validators.FormatDateValidator;

/**
 * @author wuxii@foxmail.com
 */
@Documented
@Retention(RUNTIME)
@Constraint(validatedBy = { FormatDateValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
public @interface FormatDate {

    /**
     * 日期格式
     * 
     * @return
     */
    String pattern();

    String message() default "{com.harmony.umbrella.validator.constraints.FormatDate.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
