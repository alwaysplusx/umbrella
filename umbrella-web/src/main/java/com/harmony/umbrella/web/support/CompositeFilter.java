package com.harmony.umbrella.web.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * copy from mossle
 * <p>
 * 组合web.xml中的filter
 */
public class CompositeFilter implements Filter {

    private final List<Filter> filters = new ArrayList<Filter>();

    public void init(FilterConfig config) throws ServletException {
        // 1-2-3
        for (Filter filter : filters) {
            filter.init(config);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        new VirtualFilterChain(chain, filters).doFilter(request, response);
    }

    public void destroy() {
        // 3-2-1
        for (int i = filters.size(); i-- > 0;) {
            Filter filter = filters.get(i);
            filter.destroy();
        }
    }

    public void setFilters(List<Filter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
    }

    private static final class VirtualFilterChain implements FilterChain {

        private final FilterChain originalChain;
        private Iterator<Filter> filterIterator;
        
        private VirtualFilterChain(FilterChain chain, List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.filterIterator = additionalFilters.iterator();
        }

        public void doFilter(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
            if (filterIterator.hasNext()) {
                filterIterator.next().doFilter(request, response, this);
            }else{
                originalChain.doFilter(request, response);
            }
        }
    }
}
