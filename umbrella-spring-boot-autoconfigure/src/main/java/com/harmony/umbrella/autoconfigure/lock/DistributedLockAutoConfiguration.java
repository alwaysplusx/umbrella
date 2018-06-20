package com.harmony.umbrella.autoconfigure.lock;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.integration.support.locks.LockRegistry;

import com.harmony.umbrella.autoconfigure.lock.DistributedLockAutoConfiguration.DistributedLockConfigurationImportSelector;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnClass(LockRegistry.class)
@Import(DistributedLockConfigurationImportSelector.class)
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@ConditionalOnProperty(prefix = "harmony.lock", value = "enabled", matchIfMissing = true)
public class DistributedLockAutoConfiguration {

    static class DistributedLockConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            CacheType[] types = CacheType.values();
            String[] imports = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                imports[i] = DistributedLockConfigurations.getConfigurationClass(types[i]);
            }
            return imports;
        }

    }
}
