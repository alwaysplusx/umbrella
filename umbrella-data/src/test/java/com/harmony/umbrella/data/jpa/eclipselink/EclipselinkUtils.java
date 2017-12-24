package com.harmony.umbrella.data.jpa.eclipselink;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializer;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
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
public class EclipselinkUtils {

    public static PersistenceUnitInfo loadDefault(String name, Map map) {
        CustomInitializer initializer = CustomInitializer.getInstance();
        SEPersistenceUnitInfo info = initializer.findPersistenceUnitInfo(name, map);
        if (info == null) {
            return null;
        }
        Map mergeProperties = PropertiesUtils.mergeProperties(info.getProperties(), map);
        return new EclipsePersistenceUnitInfo(mergeProperties, info);
    }

    private static class CustomInitializer extends JavaSECMPInitializer {

        public CustomInitializer(ClassLoader loader) {
            super();
            this.initializationClassloader = loader;
        }

        private static CustomInitializer getInstance() {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            JavaSECMPInitializer.getJavaSECMPInitializer(loader);
            return new CustomInitializer(loader);
        }

        @Override
        public boolean isPersistenceProviderSupported(String providerClassName) {
            return super.isPersistenceProviderSupported(providerClassName) || PersistenceProvider.class.getName().equals(providerClassName);
        }

    }

    private static class EclipsePersistenceUnitInfo extends SimplePersistenceUnitInfo implements PersistenceUnitInfo {

        private PropertyManager pm;
        private SEPersistenceUnitInfo seinfo;

        private EclipsePersistenceUnitInfo(Map properties, SEPersistenceUnitInfo unit) {
            super(unit);
            this.pm = new SimplePropertyManager(PropertiesUtils.mergeProperties(properties, unit.getProperties()));
            this.seinfo = unit;
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
            Collection<String> jarFiles = seinfo != null && seinfo.getJarFiles() != null ? seinfo.getJarFiles() : null;
            if (jarFiles == null || jarFiles.isEmpty()) {
                return Collections.emptySet();
            }
            Set<URL> jarUrls = JarLoader.findJars(jarFiles.toArray(new String[jarFiles.size()]));
            if (jarUrls.isEmpty()) {
                StaticLogger.warn("can't found any jars with [%s]", jarFiles);
            }
            return jarUrls;
        }

    }
}
