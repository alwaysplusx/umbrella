package com.harmony.umbrella.ee.formatter;

import static com.harmony.umbrella.ee.JndiConstanst.*;

import java.util.Collection;
import java.util.Set;

import com.harmony.umbrella.config.ParamManager;
import com.harmony.umbrella.ee.util.EJBUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableJndiFormatter extends AbstractJndiFormatter {

    private ParamManager paramManager;

    private String propertyDelimiter = ",";

    private FormatterFactory formatterFactory;

    public ConfigurableJndiFormatter() {
    }

    public ConfigurableJndiFormatter(ParamManager paramManager, FormatterFactory formatterFactory) {
        this.paramManager = paramManager;
        this.formatterFactory = formatterFactory;
    }

    @Override
    protected Formatter getFormatter(String pattern) {
        return formatterFactory.getFormatter(pattern);
    }

    @Override
    protected Collection<String> getPatterns() {
        return asSet(paramManager.getString(JNDI_PATTERN));
    }

    @Override
    protected Collection<String> getSeparators() {
        return asSet(paramManager.getString(JNDI_SEPARATOR));
    }

    @Override
    protected String getGlobalNamespace() {
        return paramManager.getString(JNDI_GLOBAL);
    }

    public ParamManager getParamManager() {
        return paramManager;
    }

    public void setParamManager(ParamManager paramManager) {
        this.paramManager = paramManager;
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
