package com.harmony.umbrella.data.jpa;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.harmony.umbrella.log.StaticLogger;
import com.harmony.umbrella.util.XmlUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PersistenceXml {

    static final Map<String, PersistenceUnitXmlDescriptor> persistenceUnits = new HashMap<>();

    static void init() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:META-INF/persistence.xml");
            for (Resource resource : resources) {
                InputStream is = null;
                try {
                    is = resource.getInputStream();
                    Document doc = XmlUtils.getDocument(is, true);
                    Element[] elements = XmlUtils.getElements(doc, "persistence/persistence-unit");
                    for (Element element : elements) {
                        PersistenceUnitXmlDescriptor unit = parse(element);
                        if (unit != null) {
                            persistenceUnits.put(unit.persistenceUnitName, unit);
                        }
                    }
                } catch (Exception e) {
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        } catch (IOException e) {
            StaticLogger.info("can't found any persistence in classpath");
        }
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
            String s = XmlUtils.getAttribute(element, "@transaction-type");
            transactionType = s == null ? PersistenceUnitTransactionType.RESOURCE_LOCAL : PersistenceUnitTransactionType.valueOf(s);
            s = XmlUtils.getContent(element, "shared-cache-mode");
            sharedCacheMode = s == null ? SharedCacheMode.ALL : SharedCacheMode.valueOf(s);
            s = XmlUtils.getContent(element, "validation-mode");
            validationMode = s == null ? ValidationMode.AUTO : ValidationMode.valueOf(s);
            excludeUnlistedClasses = Boolean.valueOf(XmlUtils.getContent(element, "exclude-unlisted-classes"));
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
            this.mappingFiles = mappingFiles;
            this.jarFiles = jarFiles;
            this.classes = classes;
            this.transactionType = transactionType;
            this.sharedCacheMode = sharedCacheMode;
            this.validationMode = validationMode;
            this.properties = properties;
        }

    }
}
