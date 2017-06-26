package com.harmony.umbrella.data.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.xml.xpath.XPathException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.w3c.dom.Element;

import com.harmony.umbrella.log.StaticLogger;
import com.harmony.umbrella.util.XmlUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PersistenceXml {

    static final Map<String, PersistenceUnitXmlDescriptor> persistenceUnits = new HashMap<>();

    static {
        init();
    }

    static void init() {
        List<PersistenceUnitXmlDescriptor> units = loadAll(ClassUtils.getDefaultClassLoader());
        for (PersistenceUnitXmlDescriptor unit : units) {
            persistenceUnits.put(unit.persistenceUnitName, unit);
        }
    }

    public static PersistenceUnitXmlDescriptor load(String name, ClassLoader classLoader) {
        List<PersistenceUnitXmlDescriptor> units = load(new PathMatchingResourcePatternResolver(classLoader), new PersistenceXmlFilter() {

            @Override
            public boolean accept(PersistenceUnitXmlDescriptor unit) {
                return unit.persistenceUnitName.equals(name);
            }
        });
        if (units.size() > 1) {
            throw new IllegalStateException("duplicate persistence unit found");
        }
        return units.isEmpty() ? null : units.get(0);
    }

    public static List<PersistenceUnitXmlDescriptor> loadAll(ClassLoader classLoader) {
        return loadAll(new PathMatchingResourcePatternResolver(classLoader));
    }

    public static List<PersistenceUnitXmlDescriptor> loadAll(ResourcePatternResolver resolver) {
        return load(resolver, new PersistenceXmlFilter() {

            @Override
            public boolean accept(PersistenceUnitXmlDescriptor unit) {
                return true;
            }
        });
    }

    public static List<PersistenceUnitXmlDescriptor> load(ResourcePatternResolver resolver, PersistenceXmlFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("persistence xml filter is null");
        }
        List<PersistenceUnitXmlDescriptor> result = new ArrayList<>();
        try {
            Resource[] resources = resolver.getResources("classpath*:META-INF/persistence.xml");
            if (resources.length == 0) {
                return result;
            }
            for (Resource resource : resources) {
                InputStream is = null;
                try {
                    is = resource.getInputStream();
                    Element[] elements = XmlUtils.getElements(XmlUtils.getDocument(is, true), "persistence/persistence-unit");
                    for (Element element : elements) {
                        PersistenceUnitXmlDescriptor unit = parse(element);
                        if (unit != null && filter.accept(unit)) {
                            result.add(unit);
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException) e;
                    }
                    StaticLogger.warn("can't read persistence.xml %s", resource.getURL());
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }

            }
        } catch (IOException e) {
            StaticLogger.info("can't found any persistence xml in classpath");
        }
        return result;
    }

    private static PersistenceUnitXmlDescriptor parse(Element element) {
        String persistenceUnitName, providerClassName;

        PersistenceUnitTransactionType transactionType;
        SharedCacheMode sharedCacheMode;
        ValidationMode validationMode;

        boolean excludeUnlistedClasses;
        List<String> mappingFiles, jarFiles, classes;
        Map<String, String> properties;
        try {
            persistenceUnitName = XmlUtils.getAttribute(element, "@name");
            providerClassName = XmlUtils.getContent(element, "provider");
            excludeUnlistedClasses = Boolean.valueOf(XmlUtils.getContent(element, "exclude-unlisted-classes"));

            String s = XmlUtils.getAttribute(element, "@transaction-type");
            transactionType = s == null ? PersistenceUnitTransactionType.RESOURCE_LOCAL : PersistenceUnitTransactionType.valueOf(s);
            s = XmlUtils.getContent(element, "shared-cache-mode");
            sharedCacheMode = s == null ? SharedCacheMode.ALL : SharedCacheMode.valueOf(s);
            s = XmlUtils.getContent(element, "validation-mode");
            validationMode = s == null ? ValidationMode.AUTO : ValidationMode.valueOf(s);

            mappingFiles = parseElement(element, "mapping-file");
            jarFiles = parseElement(element, "jar-file");
            classes = parseElement(element, "class");

            properties = parseProperties(element);

            return new PersistenceUnitXmlDescriptor(persistenceUnitName, providerClassName, excludeUnlistedClasses, mappingFiles, jarFiles, classes,
                    transactionType, sharedCacheMode, validationMode, properties);
        } catch (XPathException e) {

        }
        return null;
    }

    private static Map<String, String> parseProperties(Element element) throws XPathException {
        Map<String, String> properties = new LinkedHashMap<>();
        Element[] elements = XmlUtils.getElements(element, "properties/property");
        for (Element ele : elements) {
            String name = XmlUtils.getAttribute(ele, "@name");
            String value = XmlUtils.getAttribute(ele, "@value");
            properties.put(name, value);
        }
        return properties;
    }

    private static List<String> parseElement(Element element, String xpath) throws XPathException {
        List<String> result = new ArrayList<String>();
        Element[] elements = XmlUtils.getElements(element, xpath);
        for (Element ele : elements) {
            result.add(ele.getTextContent().trim());
        }
        return result;
    }

    public interface PersistenceXmlFilter {

        boolean accept(PersistenceUnitXmlDescriptor unit);

    }

    public static class PersistenceUnitXmlDescriptor {

        public final String persistenceUnitName;
        public final String providerClassName;
        public final boolean excludeUnlistedClasses;
        public final List<String> mappingFiles;
        public final List<String> jarFiles;
        public final List<String> classes;
        public final PersistenceUnitTransactionType transactionType;
        public final SharedCacheMode sharedCacheMode;
        public final ValidationMode validationMode;
        public final Map<String, String> properties;

        public PersistenceUnitXmlDescriptor(String persistenceUnitName, String providerClassName, boolean excludeUnlistedClasses, List<String> mappingFiles,
                List<String> jarFiles, List<String> classes, PersistenceUnitTransactionType transactionType, SharedCacheMode sharedCacheMode,
                ValidationMode validationMode, Map<String, String> properties) {
            this.persistenceUnitName = persistenceUnitName;
            this.providerClassName = providerClassName;
            this.excludeUnlistedClasses = excludeUnlistedClasses;
            this.mappingFiles = Collections.unmodifiableList(mappingFiles);
            this.jarFiles = Collections.unmodifiableList(jarFiles);
            this.classes = Collections.unmodifiableList(classes);
            this.transactionType = transactionType;
            this.sharedCacheMode = sharedCacheMode;
            this.validationMode = validationMode;
            this.properties = Collections.unmodifiableMap(properties);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((persistenceUnitName == null) ? 0 : persistenceUnitName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PersistenceUnitXmlDescriptor other = (PersistenceUnitXmlDescriptor) obj;
            if (persistenceUnitName == null) {
                if (other.persistenceUnitName != null)
                    return false;
            } else if (!persistenceUnitName.equals(other.persistenceUnitName))
                return false;
            return true;
        }

    }
}
