package com.harmony.umbrella.web.bind.annotation;

import java.util.Collection;

import org.springframework.data.domain.Page;

public enum PatternConverter {
    AUTO {
        @Override
        public String[] convert(String[] patterns) {
            return OBJECT.convert(patterns);
        }
    },
    PAGE {
        @Override
        public String[] convert(String[] patterns) {
            String[] result = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                String p = patterns[i];
                if (p.startsWith("$.")) {
                    result[i] = p.substring(2);
                } else {
                    result[i] = "content[*]." + p;
                }
            }
            return result;
        }
    },
    COLLECTION {
        @Override
        public String[] convert(String[] patterns) {
            String[] result = new String[patterns.length];
            for (int i = 0; i < patterns.length; i++) {
                String p = patterns[i];
                if (p.startsWith("$.")) {
                    result[i] = p.substring(2);
                } else {
                    result[i] = "[*]." + p;
                }
            }
            return result;
        }
    },
    OBJECT {
        @Override
        public String[] convert(String[] patterns) {
            String[] result = new String[patterns.length];
            System.arraycopy(patterns, 0, result, 0, patterns.length);
            return result;
        }
    };

    public abstract String[] convert(String[] patterns);

    public static PatternConverter suitableConverter(Class<?> clazz) {
        if (clazz.isAssignableFrom(Page.class)) {
            return PAGE;
        } else if (clazz.isAssignableFrom(Collection.class) || clazz.isArray()) {
            return PatternConverter.COLLECTION;
        }
        return PatternConverter.OBJECT;
    }

    public static String[] convert(Class<?> clazz, String[] patterns) {
        return suitableConverter(clazz).convert(patterns);
    }

}
