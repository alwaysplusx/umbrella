package com.harmony.umbrella.monitor;

/**
 * http监控结果，包含http请求的参数，url, 客户端地址等信息
 */
public interface HttpGraph extends Graph {

    /**
     * 监控{@link javax.servlet.http.HttpServletRequest}中对于的属性key
     */
    String HTTP_PROPERTY = HttpGraph.class.getName() + ".HTTP_PROPERTY";

    /**
     * 对应请求的http方法 GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE
     * 
     * @return Http的方法名称
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    String getHttpMethod();

    /**
     * 发起请求的地址
     * 
     * @return remote address
     * @see javax.servlet.http.HttpServletRequest#getRemoteAddr()
     */
    String getRemoteAddr();

    /**
     * 相应的地址
     * 
     * @return local server address
     * @see javax.servlet.http.HttpServletRequest#getLocalAddr()
     */
    String getLocalAddr();

    /**
     * 请求时候带的查询字符串
     * 
     * @return query string, url after '?'
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    String getQueryString();

    /**
     * 应答的状态码
     * 
     * @return response code
     * @see javax.servlet.http.HttpServletResponse#getStatus()
     */
    int getStatus();

}