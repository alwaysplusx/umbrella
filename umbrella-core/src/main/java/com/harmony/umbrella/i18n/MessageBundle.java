package com.harmony.umbrella.i18n;

import java.util.Locale;

/**
 * 国际化加载工具
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageBundle {

    String getString(String key);

    Locale getLocale();

}