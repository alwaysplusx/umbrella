package com.harmony.umbrella.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 国际化加载工具
 * 
 * @author wuxii@foxmail.com
 */
public class MessageBundle {

    private static MessageBundle INSTANCE;

    private final List<SortableLanguagePackProvider> providers = new CopyOnWriteArrayList<>();

    private MessageBundle() {
    }

    public static MessageBundle getInstance() {
        if (INSTANCE == null) {
            synchronized (MessageBundle.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageBundle();
                }
            }
        }
        return INSTANCE;
    }

    public String getText(Locale locale, String name) {
        return getLanguageBundle(locale).getText(name);
    }

    public String getText(Locale locale, String name, Object... args) {
        return getLanguageBundle(locale).getText(name, args);
    }

    public LanguageBundle getLanguageBundle(Locale locale) {
        return getLanguageBundle(locale, null);
    }

    public LanguageBundle getLanguageBundle(Locale locale, TextFormat format) {
        LanguagePack pack = getLanguagePack(locale);
        return new LanguageBundle(locale, pack, format == null ? TextFormatImpl.INSTANCE : format);
    }

    public Set<Locale> getAvailableLanguages() {
        Set<Locale> results = new HashSet<>();
        for (LanguagePackProvider provider : providers) {
            results.addAll(provider.getAvailableLanguages());
        }
        return results;
    }

    public LanguagePack getLanguagePack(Locale language) {
        if (language == null) {
            throw new IllegalArgumentException("language must not null");
        }
        Map<Locale, List<LanguagePackProvider>> temp = new LinkedHashMap<>();
        LanguagePackProvider bestProvider = null;
        Locale bestLanguage = language;

        for (LanguagePackProvider povider : providers) {
            Set<Locale> languages = povider.getAvailableLanguages();
            if (languages.contains(language)) {
                bestProvider = povider;
                break;
            }
            for (Locale lang : languages) {
                List<LanguagePackProvider> list = temp.get(lang);
                if (list == null) {
                    list = new ArrayList<>();
                    temp.put(lang, list);
                }
                list.add(povider);
            }
        }
        if (bestProvider == null) {
            bestLanguage = findBestLocale(language, temp.keySet());
            if (bestLanguage == null) {
                throw new UnsupportedLanguageException(language.toString());
            }
            bestProvider = temp.get(bestLanguage).get(0);
        }
        return bestProvider.getLanguagePack(bestLanguage);
    }

    public void registerProvider(LanguagePackProvider provider) {
        registerProvider(provider, 5);
    }

    public void registerProvider(LanguagePackProvider provider, int priority) {
        providers.add(new SortableLanguagePackProvider(provider, priority));
        Collections.sort(providers);
    }

    public boolean unregisterProvider(LanguagePackProvider provider) {
        return providers.remove(provider);
    }

    /**
     * 将资源打包成语言包
     * 
     * @param locale
     *            资源所对应的语言
     * @param props
     *            资源
     * @return 语言包
     */
    public static LanguagePack pack(Locale locale, Map<String, String> props) {
        return new LanguagePackImpl(locale, props);
    }

    /**
     * 在targets中找出与origin最相符合的locale
     * 
     * @param origin
     *            原locale
     * @param targets
     *            目标locales
     * @return 最相互匹配的locale
     */
    static Locale findBestLocale(Locale origin, Set<Locale> targets) {
        List<Locale> sameLanguage = new ArrayList<>();
        List<Locale> sameLanguageAndCountry = new ArrayList<>();

        for (Locale target : targets) {
            if (target.equals(origin)) {
                return target;
            }
            if (target.getLanguage().equalsIgnoreCase(origin.getLanguage())) {
                sameLanguage.add(target);
                if (target.getCountry().equalsIgnoreCase(origin.getCountry())) {
                    sameLanguageAndCountry.add(target);
                }
            }
        }

        return sameLanguageAndCountry.isEmpty() //
                ? (sameLanguage.isEmpty() ? null : sameLanguage.get(0)) //
                : sameLanguageAndCountry.get(0);
    }

    public static Locale toLocale(String locale) {
        if (locale == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(locale, "-_");
        return new Locale(//
                st.nextToken(), //
                st.hasMoreTokens() ? st.nextToken() : "", //
                st.hasMoreTokens() ? st.nextToken() : ""//
        );
    }

    private static final class TextFormatImpl implements TextFormat {

        private static final TextFormatImpl INSTANCE = new TextFormatImpl();

        @Override
        public String format(String pattern, Object... args) {
            return args == null || args.length == 0 ? pattern : String.format(pattern, args);
        }

    }

    public interface TextFormat {

        String format(String pattern, Object... args);

    }

    public static final class LanguageBundle {

        private Locale locale;
        private TextFormat format;
        private LanguagePack pack;

        protected LanguageBundle(Locale locale, LanguagePack pack, TextFormat format) {
            this.locale = locale;
            this.pack = pack;
            this.format = format;
        }

        public String getText(String name) {
            return getText(name, (Object[]) null);
        }

        public String getText(String name, Object... args) {
            String text = pack.getPack().get(name);
            return format.format(text, args);
        }

        public Locale getLocale() {
            return locale;
        }

        public boolean contains(String name) {
            return false;
        }

    }

    private static class SortableLanguagePackProvider implements LanguagePackProvider, Comparable<SortableLanguagePackProvider> {

        private int priority;
        private LanguagePackProvider provider;

        public SortableLanguagePackProvider(LanguagePackProvider provider, int priority) {
            this.priority = priority;
            this.provider = provider;
        }

        @Override
        public Set<Locale> getAvailableLanguages() {
            return provider.getAvailableLanguages();
        }

        @Override
        public LanguagePack getLanguagePack(Locale locale) {
            return provider.getLanguagePack(locale);
        }

        @Override
        public int compareTo(SortableLanguagePackProvider o) {
            return Integer.compare(priority, o.priority);
        }

    }

    private static final class LanguagePackImpl implements LanguagePack {

        private static final long serialVersionUID = 6825476784525171392L;

        private Locale locale;
        private Map<String, String> props;

        public LanguagePackImpl(Locale locale, Map<String, String> props) {
            this.locale = locale;
            this.props = props;
        }

        @Override
        public Locale getLocale() {
            return locale;
        }

        @Override
        public Map<String, String> getPack() {
            return Collections.unmodifiableMap(props);
        }

        @Override
        public String toString() {
            return "language pack (" + locale + ")";
        }

    }
}