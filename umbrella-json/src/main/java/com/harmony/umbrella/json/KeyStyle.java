package com.harmony.umbrella.json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.alibaba.fastjson.serializer.NameFilter;

/**
 * @author wuxii@foxmail.com
 */
public enum KeyStyle implements NameFilter {

    ORIGIN {

        @Override
        protected String convert(NameHandler name) {
            return name.getName();
        }

    }, //
    CAMEL_CASE {

        @Override
        protected String convert(NameHandler name) {
            return uncapitalizeFirst(name.getName());
        }

    }, //
    PASCAL_CASE {

        @Override
        protected String convert(NameHandler name) {
            return capitalizeFirst(name.getName());
        }

    }, //
    UNDER_LINE {

        @Override
        protected String convert(NameHandler name) {
            return name.join("_", true);
        }

    },
    UPPER_CASE {

        @Override
        protected String convert(NameHandler name) {
            return name.getName().toUpperCase();
        }

    },
    LOWER_CASE {

        @Override
        protected String convert(NameHandler name) {
            return name.getName().toLowerCase();
        }

    },
    DOT_CASE {

        @Override
        protected String convert(NameHandler name) {
            return name.join(".", true);
        }

    };

    @Override
    public String process(Object object, String name, Object value) {
        return convert(new NameHandler(name));
    }

    protected abstract String convert(NameHandler name);

    private static class NameHandler implements Iterable<String> {

        private String name;

        public NameHandler(String name) {
            this.name = name;
        }

        public List<String> getTokens() {
            int index = 0;
            List<String> result = new ArrayList<>();
            for (int i = 1, max = name.length(); i < max; i++) {
                if (Character.isUpperCase(name.charAt(i))) {
                    result.add(name.substring(index, i));
                    index = i;
                }
            }
            result.add(name.substring(index));
            return result;
        }

        protected String join(String split, boolean tolower) {
            StringBuilder o = new StringBuilder();
            Iterator<String> it = iterator();
            while (it.hasNext()) {
                String s = it.next();
                o.append(tolower ? s.toLowerCase() : s);
                if (it.hasNext()) {
                    o.append("_");
                }
            }
            return o.toString();
        }

        public String getName() {
            return name;
        }

        @Override
        public Iterator<String> iterator() {
            return getTokens().iterator();
        }

    }

    private static String capitalizeFirst(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String uncapitalizeFirst(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

}
