package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public class MonitorServiceManager {

    private static MonitorServiceManager INSTANCE;

    private List<HttpMonitor> httpMonitor = new ArrayList<HttpMonitor>();
    private List<MethodMonitor> methodMonitor = new ArrayList<MethodMonitor>();

    private Set<String> urlPattern = new HashSet<String>();
    private Set<String> urlResource = new HashSet<String>();

    private Set<String> methodPattern = new HashSet<String>();
    private Set<Method> methodResource = new HashSet<Method>();

    private MonitorServiceManager() {
    }

    public static MonitorServiceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (INSTANCE) {
                if (INSTANCE == null) {
                    INSTANCE = new MonitorServiceManager();
                }
            }
        }
        return INSTANCE;
    }

    public void addHttpMonitor(HttpMonitor monitor) {
        httpMonitor.add(monitor);
    }

    public void addMethodMonitor(MethodMonitor monitor) {
        methodMonitor.add(monitor);
    }

    public boolean addUrlPattern(String pattern) {
        return urlPattern.add(pattern);
    }

    public boolean addUrlResource(String resource) {
        return urlResource.add(resource);
    }

    public boolean addMethodPattern(String pattern) {
        return methodPattern.add(pattern);
    }

    public boolean addMethodResource(Method resource) {
        return methodResource.add(resource);
    }

    public void refreshMethodMonitor() {
        for (MethodMonitor mm : methodMonitor) {
            Set<String> patterns = mm.getPatterns();
            Set<Method> resources = mm.getResources();
            patterns.clear();
            resources.clear();

            patterns.addAll(methodPattern);
            resources.addAll(methodResource);
        }
    }

    public void refreshHttpMonitor() {
        for (HttpMonitor hm : httpMonitor) {
            Set<String> patterns = hm.getPatterns();
            Set<String> resources = hm.getResources();
            patterns.clear();
            resources.clear();

            patterns.addAll(urlPattern);
            resources.addAll(urlResource);
        }
    }

}
