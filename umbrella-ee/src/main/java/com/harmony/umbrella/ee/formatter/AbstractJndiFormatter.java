package com.harmony.umbrella.ee.formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJndiFormatter implements JndiFormatter {

    private boolean sortResult;

    private static final Log log = Logs.getLog(AbstractJndiFormatter.class);

    @Override
    public Collection<String> format(final Collection<String> beanNames, final Collection<Class> beanInterfaces) {
        List<String> result = new ArrayList<String>();

        Collection<String> patterns = getPatterns();

        if (patterns == null || patterns.isEmpty()) {
            log.warn("unspecified pattern for formatter");
            return result;
        }

        final String globalNamespace = globalNamespace();
        final Collection<String> separators = getSeparators();

        String[] beanNameArray = null;
        String[] separatorArray = null;
        Class[] beanInterfaceArray = null;

        if (beanNames == null || beanNames.isEmpty()) {
            beanNameArray = new String[] { null };
        } else {
            beanNameArray = beanNames.toArray(new String[beanNames.size()]);
        }

        if (separators == null || separators.isEmpty()) {
            separatorArray = new String[] { null };
        } else {
            separatorArray = separators.toArray(new String[separators.size()]);
        }

        if (beanInterfaces == null || beanInterfaces.isEmpty()) {
            beanInterfaceArray = new Class[] { null };
        } else {
            beanInterfaceArray = beanInterfaces.toArray(new Class[beanInterfaces.size()]);
        }

        for (String pattern : patterns) {
            Formatter fmt = getFormatter(pattern);
            for (String beanName : beanNameArray) {
                for (String separator : separatorArray) {
                    for (Class beanInterface : beanInterfaceArray) {
                        String jndi = fmt.doFormat(globalNamespace, beanName, separator, beanInterface);
                        if (!result.contains(jndi)) {
                            result.add(jndi);
                        }
                    }
                }
            }
        }

        if (sortResult) {
            Collections.sort(result);
        }

        return result;
    }

    private String globalNamespace() {
        String globalNamespace = getGlobalNamespace();
        return globalNamespace == null ? "" : globalNamespace;
    }

    protected abstract Formatter getFormatter(String pattern);

    protected abstract Collection<String> getPatterns();

    protected abstract Collection<String> getSeparators();

    protected abstract String getGlobalNamespace();

    public boolean isSortResult() {
        return sortResult;
    }

    public void setSortResult(boolean sortResult) {
        this.sortResult = sortResult;
    }

    public interface Formatter {

        String getPattern();

        String doFormat(String globalNamespace, String beanName, String separator, Class<?> beanInterface);

    }

}
