package com.harmony.umbrella.ee.formatter;

import java.util.Collection;

/**
 * @author wuxii@foxmail.com
 */
public class PatternJndiFormatter extends AbstractJndiFormatter {

    private Collection<String> patterns;

    private Collection<String> separators;

    private String globalNamespace = "";

    private FormatterFactory formatterFactory;

    public PatternJndiFormatter() {
        this.setSortResult(true);
    }

    public PatternJndiFormatter(String globalNamespace, Collection<String> patterns, Collection<String> separators) {
        this.setGlobalNamespace(globalNamespace);
        this.setPatterns(patterns);
        this.setSeparators(separators);
        this.setSortResult(true);
    }

    @Override
    protected Formatter getFormatter(String pattern) {
        return formatterFactory.getFormatter(pattern);
    }

    @Override
    public Collection<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Collection<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Collection<String> getSeparators() {
        return separators;
    }

    public void setSeparators(Collection<String> separators) {
        this.separators = separators;
    }

    @Override
    protected String getGlobalNamespace() {
        return globalNamespace;
    }

    public void setGlobalNamespace(String globalNamespace) {
        if (globalNamespace == null) {
            globalNamespace = "";
        } else {
            globalNamespace.trim();
        }
        this.globalNamespace = globalNamespace + (!globalNamespace.endsWith("/") ? "/" : "");
    }

    public FormatterFactory getFormatterFactory() {
        return formatterFactory;
    }

    public void setFormatterFactory(FormatterFactory formatterFactory) {
        this.formatterFactory = formatterFactory;
    }

}
