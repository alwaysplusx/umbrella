package com.harmony.umbrella.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceMessageBundle implements MessageBundle {

    private ResourceBundle resourceBundle;
    private Locale locale;

    public ResourceMessageBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        this.locale = resourceBundle.getLocale();
    }

    public ResourceMessageBundle(String baseName, Locale locale) {
        this.resourceBundle = ResourceBundle.getBundle(baseName, locale);
        this.locale = locale;
    }

    @Override
    public String getString(String key) {
        return resourceBundle.getString(key);
    }

    @Override
    public String getString(String key, Object... objects) {
        String text = resourceBundle.getString(key);
        return String.format(text, objects);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

}
