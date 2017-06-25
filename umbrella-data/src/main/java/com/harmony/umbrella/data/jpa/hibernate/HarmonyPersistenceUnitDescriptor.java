package com.harmony.umbrella.data.jpa.hibernate;

import java.net.URL;
import java.util.ArrayList;
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
public class HarmonyPersistenceUnitDescriptor implements PersistenceUnitDescriptor {

    private PersistenceUnitDescriptor master;
    private PersistenceUnitDescriptor slave;

    public HarmonyPersistenceUnitDescriptor(PersistenceUnitDescriptor master, PersistenceUnitDescriptor slave) {
        if (master == null) {
            throw new IllegalArgumentException("master persistence unit descriptor is null");
        }
        this.master = master;
        this.slave = slave;
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return master.getPersistenceUnitRootUrl();
    }

    @Override
    public String getName() {
        return master.getName();
    }

    @Override
    public String getProviderClassName() {
        return master.getProviderClassName();
    }

    @Override
    public boolean isUseQuotedIdentifiers() {
        return master.isUseQuotedIdentifiers();
    }

    @Override
    public boolean isExcludeUnlistedClasses() {
        return master.isExcludeUnlistedClasses();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return master.getTransactionType();
    }

    @Override
    public ValidationMode getValidationMode() {
        return master.getValidationMode();
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return master.getSharedCacheMode();
    }

    @Override
    public List<String> getManagedClassNames() {
        List<String> result = new ArrayList<>();
        if (master.getManagedClassNames() != null) {
            result.addAll(master.getManagedClassNames());
        }
        if (slave != null && slave.getManagedClassNames() != null) {
            result.addAll(slave.getManagedClassNames());
        }
        return result;
    }

    @Override
    public List<String> getMappingFileNames() {
        List<String> result = new ArrayList<>();
        if (master.getMappingFileNames() != null) {
            result.addAll(master.getMappingFileNames());
        }
        if (slave != null && slave.getMappingFileNames() != null) {
            result.addAll(slave.getMappingFileNames());
        }
        return result;
    }

    @Override
    public List<URL> getJarFileUrls() {
        List<URL> result = new ArrayList<>();
        if (master.getJarFileUrls() != null) {
            result.addAll(master.getJarFileUrls());
        }
        if (slave != null && slave.getJarFileUrls() != null) {
            result.addAll(slave.getJarFileUrls());
        }
        return result;
    }

    @Override
    public Object getNonJtaDataSource() {
        return master.getNonJtaDataSource();
    }

    @Override
    public Object getJtaDataSource() {
        return master.getJtaDataSource();
    }

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        if (master.getProperties() != null) {
            properties.putAll(master.getProperties());
        }
        if (slave != null && slave.getProperties() != null) {
            properties.putAll(slave.getProperties());
        }
        return properties;
    }

    @Override
    public ClassLoader getClassLoader() {
        return master.getClassLoader();
    }

    @Override
    public ClassLoader getTempClassLoader() {
        return master.getTempClassLoader();
    }

    @Override
    public void pushClassTransformer(EnhancementContext enhancementContext) {
        master.pushClassTransformer(enhancementContext);
    }

}
