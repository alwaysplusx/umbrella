package com.harmony.umbrella.data.jpa.eclipselink;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class HarmonyPersistenceUnitInfo implements PersistenceUnitInfo {

    private PersistenceUnitInfo master;
    private PersistenceUnitInfo slave;

    public HarmonyPersistenceUnitInfo(PersistenceUnitInfo master, PersistenceUnitInfo slave) {
        if (master == null) {
            throw new IllegalArgumentException("master persistence unit info is not been set");
        }
        this.master = master;
        this.slave = slave;
    }

    @Override
    public String getPersistenceUnitName() {
        return master.getPersistenceUnitName();
    }

    @Override
    public String getPersistenceProviderClassName() {
        return master.getPersistenceProviderClassName();
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return master.getTransactionType();
    }

    @Override
    public DataSource getJtaDataSource() {
        return master.getJtaDataSource();
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return master.getNonJtaDataSource();
    }

    @Override
    public List<String> getMappingFileNames() {
        Set<String> result = new LinkedHashSet<>();
        if (master.getMappingFileNames() != null) {
            result.addAll(master.getMappingFileNames());
        }
        if (slave != null && slave.getMappingFileNames() != null) {
            result.addAll(slave.getMappingFileNames());
        }
        return Collections.unmodifiableList(new ArrayList(result));
    }

    @Override
    public List<URL> getJarFileUrls() {
        Set<URL> result = new LinkedHashSet<>();
        if (master.getJarFileUrls() != null) {
            result.addAll(master.getJarFileUrls());
        }
        if (slave != null && slave.getJarFileUrls() != null) {
            result.addAll(slave.getJarFileUrls());
        }
        return Collections.unmodifiableList(new ArrayList(result));
    }

    @Override
    public URL getPersistenceUnitRootUrl() {
        return master.getPersistenceUnitRootUrl();
    }

    @Override
    public List<String> getManagedClassNames() {
        Set<String> result = new LinkedHashSet<>();
        if (master.getManagedClassNames() != null) {
            result.addAll(master.getManagedClassNames());
        }
        if (slave != null && slave.getManagedClassNames() != null) {
            result.addAll(slave.getManagedClassNames());
        }
        return Collections.unmodifiableList(new ArrayList(result));
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return master.excludeUnlistedClasses();
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return master.getSharedCacheMode();
    }

    @Override
    public ValidationMode getValidationMode() {
        return master.getValidationMode();
    }

    @Override
    public Properties getProperties() {
        Properties merged = new Properties();
        if (master.getProperties() != null) {
            merged.putAll(master.getProperties());
        }
        if (slave != null && slave.getProperties() != null) {
            merged.putAll(slave.getProperties());
        }
        return merged;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return master.getPersistenceXMLSchemaVersion();
    }

    @Override
    public ClassLoader getClassLoader() {
        return master.getClassLoader();
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        master.addTransformer(transformer);
        if (slave != null) {
            slave.addTransformer(transformer);
        }
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return master.getNewTempClassLoader();
    }

}