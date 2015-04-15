package com.harmony.umbrella.validator.constraints;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.harmony.umbrella.validator.validators.ContainsValidator;

@Documented
@Retention(RUNTIME)
@Constraint(validatedBy = { ContainsValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
public @interface Contains {

    String[] contents() default {};

    boolean all() default false;

    String message() default "{com.harmony.umbrella.validator.constraints.Contains.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
