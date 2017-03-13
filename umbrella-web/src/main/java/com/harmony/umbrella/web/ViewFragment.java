package com.harmony.umbrella.web;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.servlet.View;

/**
 * @author wuxii@foxmail.com
 */
public class ViewFragment {

    public ViewFragment page(Page<?> page) {
        return this;
    }

    public ViewFragment content(List<?> content) {
        return this;
    }

    public ViewFragment model(Object m) {
        return null;
    }

    public View build() {
        return null;
    }

}
