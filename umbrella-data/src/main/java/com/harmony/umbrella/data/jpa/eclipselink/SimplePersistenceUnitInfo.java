package com.harmony.umbrella.data.jpa.eclipselink;

import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

/**
 * @author wuxii@foxmail.com
 */
public class SimplePersistenceUnitInfo implements PersistenceUnitInfo {

    protected final PersistenceUnitInfo unit;

    public SimplePersistenceUnitInfo(PersistenceUnitInfo unit) {
        this.unit = unit;
    }

    @Override
    public String getPersistenceUnitName() {
        return unit != null ? unit.getPersistenceUnitName() : null;
    }

    @Override
    public String getPersistenceProviderClassName() {
        return unit != null ? unit.getPersistenceUnitName() : null;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        return unit != null ? unit.getTransactionType() : null;
    }

    @Override
    public DataSource getJtaDataSource() {
        return unit != null ? unit.getJtaDataSource() : null;
    }

    @Override
    public DataSource getNonJtaDataSource() {
        return unit != null ? unit.getNonJtaDataSource() : null;
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
    public URL getPersistenceUnitRootUrl() {
        return unit != null ? unit.getPersistenceUnitRootUrl() : null;
    }

    @Override
    public List<String> getManagedClassNames() {
        return unit != null ? unit.getManagedClassNames() : null;
    }

    @Override
    public boolean excludeUnlistedClasses() {
        return unit != null ? unit.excludeUnlistedClasses() : null;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return unit != null ? unit.getSharedCacheMode() : null;
    }

    @Override
    public ValidationMode getValidationMode() {
        return unit != null ? unit.getValidationMode() : null;
    }

    @Override
    public Properties getProperties() {
        return unit != null ? unit.getProperties() : null;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return unit != null ? unit.getPersistenceXMLSchemaVersion() : null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return unit != null ? unit.getClassLoader() : null;
    }

    @Override
    public void addTransformer(ClassTransformer transformer) {
        if (unit != null) {
            unit.addTransformer(transformer);
        }
    }

    @Override
    public ClassLoader getNewTempClassLoader() {
        return unit != null ? unit.getNewTempClassLoader() : null;
    }

}
