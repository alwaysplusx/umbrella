package com.harmony.umbrella.autoconfigure.lock;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.support.locks.LockRegistry;

import com.harmony.umbrella.autoconfigure.lock.LockAutoConfiguration.LockConfigurationImportSelector;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnClass(LockRegistry.class)
@Import(LockConfigurationImportSelector.class)
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@ConditionalOnProperty(prefix = "harmony.lock", value = "enabled", matchIfMissing = false)
public class LockAutoConfiguration {

    static class LockConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            LockType[] types = LockType.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = LockConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }

    }
}
