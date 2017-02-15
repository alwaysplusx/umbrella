package com.harmony.umbrella.web.method;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleMethodArgumentResolverTest {

    @Test
    public void test() {
        Map<String, String[]> queryParams = new LinkedHashMap<>();
        queryParams.put("filter_EQ_username_OR_LIKE_password", new String[] { "wuxii", "abc123" });
        queryParams.put("filter_EQ_age", new String[] { "18" });
        // System.out.println(builder.bundle());
    }

}
