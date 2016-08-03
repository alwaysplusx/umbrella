package com.harmony.umbrella.ee.formatter;

import static com.harmony.umbrella.ee.JndiConstanst.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.harmony.umbrella.ee.formatter.AbstractJndiFormatter.Formatter;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultFormatterFactory implements FormatterFactory {

    private Map<String, Formatter> formatterMap = new HashMap<String, Formatter>();

    @Override
    public Formatter getFormatter(String pattern) {
        Formatter formatter = formatterMap.get(pattern);
        if (formatter == null) {
            formatter = createFormatter(pattern);
            formatterMap.put(pattern, formatter);
        }
        return formatter;
    }

    protected Formatter createFormatter(String pattern) {
        return new StringPatternFormatter(pattern);
    }

    public static String toFormatPattern(String pattern) {
        for (int i = 0, max = PATTERN_KEY_WORDS.size(); i < max; i++) {
            String word = PATTERN_KEY_WORDS.get(i);
            if (pattern.indexOf(word) != -1) {
                pattern = pattern.replace(word, "{" + i + "}");
            }
        }
        return pattern.replace(":", "");
    }

    public static class StringPatternFormatter implements AbstractJndiFormatter.Formatter {

        private String pattern;
        private String formatPattern;

        public StringPatternFormatter(String pattern) {
            this.pattern = pattern;
            this.formatPattern = toFormatPattern(pattern);
        }

        @Override
        public String doFormat(String globalNamespace, String beanName, String separator, Class<?> beanInterface) {
            if ((isNeeded(0) && globalNamespace == null) //
                    || (isNeeded(1) && beanName == null) //
                    || (isNeeded(2) && separator == null) //
                    || (isNeeded(3) && beanInterface == null)) {
                return null;
            }
            return MessageFormat.format(formatPattern, globalNamespace, beanName, separator, beanInterface == null ? null : beanInterface.getName());
        }

        protected boolean isNeeded(int index) {
            return formatPattern.indexOf("{" + index + "}") > 0;
        }

        @Override
        public String getPattern() {
            return pattern;
        }

    }
}
