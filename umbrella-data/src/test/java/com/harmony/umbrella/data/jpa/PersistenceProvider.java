package com.harmony.umbrella.data.jpa;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.data.jpa.eclipselink.EclipselinkUtils;
import com.harmony.umbrella.data.jpa.eclipselink.HarmonyPersistenceUnitInfo;
import com.harmony.umbrella.data.jpa.hibernate.HarmonyPersistenceUnitDescriptor;
import com.harmony.umbrella.data.jpa.hibernate.HibernateUtils;
import com.harmony.umbrella.log.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public class PersistenceProvider implements javax.persistence.spi.PersistenceProvider {

    private static final boolean EclipseLinkPersent = isPresent("org.eclipse.persistence.jpa.PersistenceProvider");
    private static final boolean HibernatePersent = isPresent("org.hibernate.jpa.HibernatePersistenceProvider");

    @Override
    public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
        return findProvider().createEntityManagerFactory(emName, map);
    }

    @Override
    public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map map) {
        return findProvider().createContainerEntityManagerFactory(info, map);
    }

    @Override
    public void generateSchema(PersistenceUnitInfo info, Map map) {
        findProvider().generateSchema(info, map);
    }

    @Override
    public boolean generateSchema(String persistenceUnitName, Map map) {
        return findProvider().generateSchema(persistenceUnitName, map);
    }

    @Override
    public ProviderUtil getProviderUtil() {
        return findProvider().getProviderUtil();
    }

    private javax.persistence.spi.PersistenceProvider findProvider() {
        if (HibernatePersent) {
            return HibernatePersistenceProvider.INSTANCE;
        } else if (EclipseLinkPersent) {
            return EclipsePersistenceProvider.INSTANCE;
        }
        throw new IllegalStateException("persistence provider not found");
    }

    private static boolean isPresent(String name) {
        return ClassUtils.isPresent(name, ClassUtils.getDefaultClassLoader());
    }

    private static class EclipsePersistenceProvider extends org.eclipse.persistence.jpa.PersistenceProvider {

        private static final javax.persistence.spi.PersistenceProvider INSTANCE = new EclipsePersistenceProvider();

        private EclipsePersistenceProvider() {
            StaticLogger.info("Runtime EclipseLink version [%s]", org.eclipse.persistence.Version.getVersionString());
        }

        // override from interface

        @Override
        public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo info, Map properties) {
            PersistenceUnitInfo harmonyUnitInfo = integrate(info, properties);
            return super.createContainerEntityManagerFactory(harmonyUnitInfo, properties);
        }

        @Override
        public void generateSchema(PersistenceUnitInfo info, Map properties) {
            PersistenceUnitInfo harmonyUnitInfo = integrate(info, properties);
            super.generateSchema(harmonyUnitInfo, properties);
        }

        // override eclipselink provider

        @Override
        protected EntityManagerFactoryImpl createEntityManagerFactoryImpl(PersistenceUnitInfo puInfo, Map properties, boolean requiresConnection) {
            PersistenceUnitInfo harmonyUnitInfo = integrate(puInfo, properties);
            return super.createEntityManagerFactoryImpl(harmonyUnitInfo, properties, requiresConnection);
        }

        @Override
        protected EntityManagerFactory createContainerEntityManagerFactoryImpl(PersistenceUnitInfo info, Map properties, boolean requiresConnection) {
            PersistenceUnitInfo harmonyUnitInfo = integrate(info, properties);
            return super.createContainerEntityManagerFactoryImpl(harmonyUnitInfo, properties, requiresConnection);
        }

        private PersistenceUnitInfo integrate(PersistenceUnitInfo master, Map map) {
            if (master instanceof HarmonyPersistenceUnitInfo) {
                return master;
            }
            String unitName = master.getPersistenceUnitName();
            PersistenceUnitInfo slave = EclipselinkUtils.loadDefault(unitName, map);
            return new HarmonyPersistenceUnitInfo(master, slave);
        }

    }

    private static class HibernatePersistenceProvider extends org.hibernate.jpa.HibernatePersistenceProvider {

        private static final HibernatePersistenceProvider INSTANCE = new HibernatePersistenceProvider();

        public HibernatePersistenceProvider() {
            StaticLogger.info("Runtime Hibernate version [%s]", org.hibernate.Version.getVersionString());
        }

        // override from interface

        // override parent

        @Override
        protected EntityManagerFactoryBuilder getEntityManagerFactoryBuilder(PersistenceUnitDescriptor persistenceUnitDescriptor, Map integration,
                ClassLoader providedClassLoader) {
            PersistenceUnitDescriptor harmony = integrate(persistenceUnitDescriptor, integration);
            return super.getEntityManagerFactoryBuilder(harmony, integration, providedClassLoader);
        }

        private PersistenceUnitDescriptor integrate(PersistenceUnitDescriptor master, Map map) {
            if (master instanceof HarmonyPersistenceUnitDescriptor) {
                return master;
            }
            String unitName = master.getName();
            PersistenceUnitDescriptor slave = HibernateUtils.loadDefault(unitName, map);
            return new HarmonyPersistenceUnitDescriptor(master, slave);
        }

    }

}
