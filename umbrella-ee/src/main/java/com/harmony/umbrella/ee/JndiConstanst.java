package com.harmony.umbrella.ee;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JndiConstanst {

    public static final String JNDI_GLOBAL = "jndi.global.prefix";

    public static final String JNDI_ROOT = "jndi.format.root";

    public static final String JNDI_BEAN = "jndi.format.bean";

    public static final String JNDI_SEPARATOR = "jndi.format.separator";

    public static final String JNDI_REMOTE = "jndi.format.remote";

    public static final String JNDI_PATTERN = "jndi.format.pattern";

    public static final List<String> PATTERN_KEY_WORDS;

    static {
        PATTERN_KEY_WORDS = Collections.unmodifiableList(Arrays.asList("globalNamespace", "beanName", "separator", "beanInterface"));
    }

}
