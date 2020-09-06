package com.harmony.umbrella.data.config;

import com.harmony.umbrella.data.config.support.DataAuditingHandler;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author wuxin
 * @see org.springframework.data.jpa.repository.config.EnableJpaAuditing
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DataAuditingRegistrar.class)
public @interface EnableDataAuditing {

    @AliasFor("auditingHandlerRef")
    String value() default "";

    /**
     * Configures the {@link DataAuditingHandler}
     *
     * @return
     */
    @AliasFor("value")
    String auditingHandlerRef() default "";

}
