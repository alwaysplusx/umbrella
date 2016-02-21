package com.harmony.umbrella.log;

import java.util.Map;

public interface MessageTemplateFormat {

    String format(String template, Object[] params, Map<String, Object> properties);

}