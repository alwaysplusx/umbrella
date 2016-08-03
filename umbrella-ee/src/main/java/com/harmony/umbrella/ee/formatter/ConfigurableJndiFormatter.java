package com.harmony.umbrella.ee.formatter;

import static com.harmony.umbrella.ee.JndiConstanst.*;

import java.util.Collection;

import com.harmony.umbrella.config.ParamManager;

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
        return paramManager.asSet(JNDI_PATTERN, propertyDelimiter);
    }

    @Override
    protected Collection<String> getSeparators() {
        return paramManager.asSet(JNDI_SEPARATOR, propertyDelimiter);
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

}
