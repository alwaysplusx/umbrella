package com.harmony.umbrella.monitor;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii@foxmail.com
 */
public interface MethodGraphReporter {

    void report(MethodGraph graph);

    void report(MethodGraph graph, HttpServletRequest request);

}
