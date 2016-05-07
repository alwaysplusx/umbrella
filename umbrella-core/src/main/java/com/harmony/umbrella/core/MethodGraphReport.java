package com.harmony.umbrella.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuxii@foxmail.com
 */
public interface MethodGraphReport {

    void report(MethodGraph graph);

    void report(MethodGraph graph, HttpServletRequest request, HttpServletResponse response);

}
