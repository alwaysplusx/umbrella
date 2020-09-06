package com.harmony.umbrella.data.config;

import com.harmony.umbrella.data.config.support.DataAuditingEntityListener;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author wuxin
 */
class DataAuditingRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(DataAuditingEntityListener.class);
        String auditingHandlerRef = getDataAuditingHandlerRef(annotationMetadata);
//        if (StringUtils.hasText(auditingHandlerRef)) {
//            builder.addConstructorArgReference(auditingHandlerRef);
//        }
        builder.addPropertyReference("dataAuditingHandler", auditingHandlerRef);
        AbstractBeanDefinition rbd = builder.getRawBeanDefinition();
        String id = DataAuditingEntityListener.class.getName();
        rbd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(id, rbd);
    }

    protected String getDataAuditingHandlerRef(AnnotationMetadata annotationMetadata) {
        Class<EnableDataAuditing> annotation = EnableDataAuditing.class;
        Map<String, Object> attrs = annotationMetadata.getAnnotationAttributes(annotation.getName());
        if (attrs == null) {
            throw new IllegalArgumentException(String.format("Couldn't find annotation attributes for %s in %s!", annotation, annotationMetadata));
        }
        AnnotationAttributes annAttrs = new AnnotationAttributes(attrs);
        return annAttrs.getString("auditingHandlerRef");
    }

}
