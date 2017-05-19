package com.harmony.umbrella.autoconfigure.ejb;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.harmony.umbrella.ee.formatter.FormatterFactory;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.ejb")
public class EJBProperties {

    private String contextPropertiesFileLocation;
    private Map<String, String> contextProperties;

    private Class<? extends FormatterFactory> formatterFactory;

    private List<String> namespace;
    private List<String> separator;
    private List<String> beanName;
    private List<String> beanInterface;
    private List<String> patterns;

    public Class<? extends FormatterFactory> getFormatterFactory() {
        return formatterFactory;
    }

    public void setFormatterFactory(Class<? extends FormatterFactory> formatterFactory) {
        this.formatterFactory = formatterFactory;
    }

    public String getContextPropertiesFileLocation() {
        return contextPropertiesFileLocation;
    }

    public void setContextPropertiesFileLocation(String contextPropertiesFileLocation) {
        this.contextPropertiesFileLocation = contextPropertiesFileLocation;
    }

    public Map<String, String> getContextProperties() {
        return contextProperties;
    }

    public void setContextProperties(Map<String, String> contextProperties) {
        this.contextProperties = contextProperties;
    }

    public List<String> getNamespace() {
        return namespace;
    }

    public void setNamespace(List<String> namespace) {
        this.namespace = namespace;
    }

    public List<String> getSeparator() {
        return separator;
    }

    public void setSeparator(List<String> separator) {
        this.separator = separator;
    }

    public List<String> getBeanName() {
        return beanName;
    }

    public void setBeanName(List<String> beanName) {
        this.beanName = beanName;
    }

    public List<String> getBeanInterface() {
        return beanInterface;
    }

    public void setBeanInterface(List<String> beanInterface) {
        this.beanInterface = beanInterface;
    }

    public List<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<String> patterns) {
        this.patterns = patterns;
    }

}
