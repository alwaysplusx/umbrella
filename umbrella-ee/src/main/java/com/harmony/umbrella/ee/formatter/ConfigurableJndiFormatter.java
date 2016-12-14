package com.harmony.umbrella.ee.formatter;

import static com.harmony.umbrella.ee.JndiConstanst.*;

import java.util.Collection;
import java.util.Set;

import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.ee.util.EJBUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableJndiFormatter extends AbstractJndiFormatter {

    private PropertyManager propertyManager;

    private String propertyDelimiter = ",";

    private FormatterFactory formatterFactory;

    public ConfigurableJndiFormatter() {
    }

    public ConfigurableJndiFormatter(PropertyManager propertyManager, FormatterFactory formatterFactory) {
        this.propertyManager = propertyManager;
        this.formatterFactory = formatterFactory;
    }

    @Override
    protected Formatter getFormatter(String pattern) {
        return formatterFactory.getFormatter(pattern);
    }

    @Override
    protected Collection<String> getPatterns() {
        return asSet(propertyManager.getString(JNDI_PATTERN));
    }

    @Override
    protected Collection<String> getSeparators() {
        return asSet(propertyManager.getString(JNDI_SEPARATOR));
    }

    @Override
    protected String getGlobalNamespace() {
        return propertyManager.getString(JNDI_GLOBAL);
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public String getPropertyDelimiter() {
        return propertyDelimiter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        this.propertyDelimiter = propertyDelimiter;
    }

    public FormatterFactory getFormatterFactory() {
        return formatterFactory;
    }

    public void setFormatterFactory(FormatterFactory formatterFactory) {
        this.formatterFactory = formatterFactory;
    }

    private Set<String> asSet(String text) {
        return EJBUtils.asSet(text, propertyDelimiter);
    }
}
