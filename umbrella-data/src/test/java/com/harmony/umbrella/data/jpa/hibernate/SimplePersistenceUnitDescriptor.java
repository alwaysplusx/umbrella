package com.harmony.umbrella.data.jpa.hibernate;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;

/**
 * @author wuxii@foxmail.com
 */
public class SimplePersistenceUnitDescriptor implements PersistenceUnitDescriptor {

    protected final PersistenceUnitDescriptor unit;

    public SimplePersistenceUnitDescriptor(PersistenceUnitDescriptor unit) {
        this.unit = unit;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return unit != null ? unit.getPersistenceUnitRootUrl() : null;
    }

    @Override
    public String getName() {
        return unit != null ? unit.getName() : null;
    }

    @Override
    public String getProviderClassName() {
        return unit != null ? unit.getProviderClassName() : null;
    }

    @Override
    public boolean isUseQuotedIdentifiers() {
        return unit != null ? unit.isUseQuotedIdentifiers() : null;
    }

    @Override
    public boolean isExcludeUnlistedClasses() {
        return unit != null ? unit.isExcludeUnlistedClasses() : null;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return unit != null ? unit.getTransactionType() : null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return unit != null ? unit.getValidationMode() : null;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return unit != null ? unit.getSharedCacheMode() : null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return unit != null ? unit.getManagedClassNames() : null;
    }

    @Override
    public List<String> getMappingFileNames() {
        return unit != null ? unit.getMappingFileNames() : null;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return unit != null ? unit.getJarFileUrls() : null;
    }

    @Override
    public Object getNonJtaDataSource() {
        return unit != null ? unit.getNonJtaDataSource() : null;
    }

    @Override
    public Object getJtaDataSource() {
        return unit != null ? unit.getJtaDataSource() : null;
    }

    @Override
    public Properties getProperties() {
        return unit != null ? unit.getProperties() : null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return unit != null ? unit.getClassLoader() : null;
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return unit != null ? unit.getTempClassLoader() : null;
    }

    @Override
    public void pushClassTransformer(EnhancementContext enhancementContext) {
        if (unit != null) {
            unit.pushClassTransformer(enhancementContext);
        }
    }

}
