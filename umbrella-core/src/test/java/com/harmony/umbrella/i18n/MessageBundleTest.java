package com.harmony.umbrella.i18n;

import java.io.File;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class MessageBundleTest {

    @Test
    public void test() {
        LanguagePackProvider provider = new FilesLanguagePackProvider(new File("src/test/resources/locale"));
        Set<Locale> languages = provider.getAvailableLanguages();
        System.out.println(languages);
        for (Locale locale : languages) {
            System.out.println(provider.getLanguagePack(locale));
        }
    }

}
