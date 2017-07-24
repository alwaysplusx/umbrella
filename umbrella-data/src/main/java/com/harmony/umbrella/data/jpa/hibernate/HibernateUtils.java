package com.harmony.umbrella.data.jpa.hibernate;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.jpa.boot.internal.ParsedPersistenceXmlDescriptor;
import org.hibernate.jpa.boot.internal.PersistenceXmlParser;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.hibernate.jpa.boot.spi.ProviderChecker;
import org.springframework.util.StringUtils;

import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.core.SimplePropertyManager;
import com.harmony.umbrella.data.jpa.PersistenceXmlProperties;
import com.harmony.umbrella.data.jpa.JarLoader;
import com.harmony.umbrella.data.jpa.PersistenceProvider;
import com.harmony.umbrella.log.StaticLogger;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HibernateUtils {

    public static PersistenceUnitDescriptor loadDefault(String name, Map<?, ?> properties) {
        List<ParsedPersistenceXmlDescriptor> allUnit = PersistenceXmlParser.locatePersistenceUnits(properties);
        for (ParsedPersistenceXmlDescriptor unit : allUnit) {
            String providerClassName = unit.getProviderClassName();
            if (name.equals(unit.getName())//
                    && (ProviderChecker.isProvider(unit, properties) //
                            || PersistenceProvider.class.getName().equals(providerClassName))) {
                Map mergeProperties = PropertiesUtils.mergeProperties(unit.getProperties(), (Map) properties);
                return new HibernatePersistenceUnitDescriptor(mergeProperties, unit);
            }
        }
        return null;
    }

    private static class HibernatePersistenceUnitDescriptor extends SimplePersistenceUnitDescriptor {

        private PropertyManager pm;

        public HibernatePersistenceUnitDescriptor(Map properties, PersistenceUnitDescriptor unit) {
            super(unit);
            this.pm = new SimplePropertyManager(properties);
        }

        @Override
        public List<URL> getJarFileUrls() {
            Set<URL> result = new LinkedHashSet();

            // jar-file element
            if (pm.getBoolean(PersistenceXmlProperties.PARSE_JAR_FILE, false)) {
                result.addAll(parseJarFile());
            } else {
                List<URL> urls = super.getJarFileUrls();
                if (urls != null) {
                    result.addAll(urls);
                }
            }

            // property jar-file
            String location = pm.getString(PersistenceXmlProperties.SCAN_JAR_FILE);
            if (location != null) {
                Set<URL> jarUrls = JarLoader.findJars(StringUtils.tokenizeToStringArray(location, ",", true, true));
                if (jarUrls.isEmpty()) {
                    StaticLogger.warn("can't found any jars with [%s]", location);
                }
                result.addAll(jarUrls);
            }

            return Collections.unmodifiableList(new ArrayList<>(result));
        }

        private Set<URL> parseJarFile() {
            List<URL> jarFileUrls = super.getJarFileUrls();
            if (jarFileUrls == null || jarFileUrls.isEmpty()) {
                return Collections.emptySet();
            }
            List<String> jarFiles = new ArrayList<>();
            for (URL url : jarFileUrls) {
                jarFiles.add(url.getPath());
            }
            Set<URL> jarUrls = JarLoader.findJars(jarFiles.toArray(new String[jarFiles.size()]));
            if (jarUrls.isEmpty()) {
                StaticLogger.warn("can't found any jars with [%s]", jarFiles);
            }
            return jarUrls;
        }

    }

}
