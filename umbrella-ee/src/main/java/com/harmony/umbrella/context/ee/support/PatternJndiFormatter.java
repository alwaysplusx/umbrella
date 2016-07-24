package com.harmony.umbrella.context.ee.support;

import static com.harmony.umbrella.context.ee.JndiConstanst.*;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public class PatternJndiFormatter implements JndiFormatter {

    private ConfigManager configManager;

    private static final String[] KEY_NAMES = { "globalNamespace", "beanName", "separator", "beanInterface" };

    private static final List<String> DEFAULT_PATTERNS = Arrays.asList("beanName", "beanName:separator:beanInterface");

    public PatternJndiFormatter(ConfigManager configManager) {
        this.setConfigManager(configManager);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Set<String> format(Collection<String> beanNames, Collection<Class> beanInterfaces) {
        Set<String> result = new HashSet<String>();
        Set<String> patterns = configManager.getPropertySet(JNDI_PATTERN);
        if (patterns.isEmpty()) {
            patterns.addAll(DEFAULT_PATTERNS);
        }
        Set<String> separators = configManager.getPropertySet(JNDI_SEPARATOR);
        if (separators.isEmpty()) {
            separators.add("#");
            separators.add("!");
        }
        Set<String> globals = configManager.getPropertySet(JNDI_GLOBAL);
        if (globals.isEmpty()) {
            globals.add("");
        }
        for (String p : patterns) {
            String pattern = toPattern(p);
            for (String global : globals) {
                for (String beanName : beanNames) {
                    for (String separator : separators) {
                        for (Class beanInterface : beanInterfaces) {
                            String jndi = MessageFormat.format(pattern, global, beanName, separator, beanInterface.getName());
                            result.add(jndi);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static String toPattern(String pattern) {
        for (int i = 0, max = KEY_NAMES.length; i < max; i++) {
            if (pattern.indexOf(KEY_NAMES[i]) != -1) {
                pattern = pattern.replace(KEY_NAMES[i], "{" + i + "}");
            }
        }
        return pattern.replace(":", "");
    }

    public static void main(String[] args) {
        String pattern = toPattern("globalNamespace:beanName:separator:beanInterface");
        System.out.println(pattern);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

}
