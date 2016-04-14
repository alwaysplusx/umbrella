package com.harmony.umbrella.log.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.MethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public interface LogGraphReporter {

    void report(MethodGraph graph);

    void report(MethodGraph graph, HttpServletRequest request);

}
