package com.harmony.umbrella.ee.formatter;

import com.harmony.umbrella.ee.formatter.AbstractJndiFormatter.Formatter;

/**
 * @author wuxii@foxmail.com
 */
public interface FormatterFactory {

    Formatter getFormatter(String pattern);

}
