package com.harmony.umbrella.monitor.matcher;

import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.util.AntPathMatcher;

/**
 * AntPathMatcher adapter
 * 
 * @author wuxii@foxmail.com
 * @see AntPathMatcher
 */
public class UrlPathMatcher extends AntPathMatcher implements ResourceMatcher<String> {

    @Override
    public boolean match(String pattern, String path) {
        return super.match(pattern, path);
    }

}
