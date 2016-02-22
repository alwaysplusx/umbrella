package com.harmony.umbrella.log;

import java.lang.reflect.Method;

public interface TemplateFormat {

    String format(String template, Object[] params, Method targetMethod, Object target);

}