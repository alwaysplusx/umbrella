package com.harmony.umbrella.i18n;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public interface LanguagePack extends Serializable {

    Locale getLocale();

    Map<String, String> getPack();

}
