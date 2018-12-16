package com.harmony.umbrella.web.util;

import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public abstract class WebUtils {

    public static <T> PageWrapper<T> frontendPage(Page<T> page) {
        return new PageWrapper<>(page);
    }

    public static final class PageWrapper<T> implements Serializable {

        private static final long serialVersionUID = 1L;
        private final Page<T> page;

        public PageWrapper(Page<T> page) {
            this.page = page;
        }

        public int getPage() {
            return page.getNumber();
        }

        public long getTotals() {
            return page.getTotalElements();
        }

        public List<T> getItems() {
            return page.getContent();
        }

        public Page<T> unwrap() {
            return page;
        }

    }

}
