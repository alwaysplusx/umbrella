package com.harmony.umbrella.monitor;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface PatternManager {

    Set<String> getPattern(MonitorPolicy policy);

    boolean contains(String pattern, MonitorPolicy policy);

    void addPattern(String pattern, MonitorPolicy policy);

    void removePattern(String pattern, MonitorPolicy policy);

}
