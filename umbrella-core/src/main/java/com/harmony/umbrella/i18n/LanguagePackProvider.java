package com.harmony.umbrella.i18n;

import java.util.Locale;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface LanguagePackProvider {

    Set<Locale> getAvailableLanguages();

    LanguagePack getLanguagePack(Locale locale);

}
