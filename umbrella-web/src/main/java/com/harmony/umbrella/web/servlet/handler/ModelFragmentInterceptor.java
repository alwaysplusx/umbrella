package com.harmony.umbrella.web.servlet.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.harmony.umbrella.web.method.support.BundleParamMethodArgumentResolver;
import com.harmony.umbrella.web.method.support.ModelFragment;
import com.harmony.umbrella.web.servlet.view.ModelResourceView;

/**
 * @author wuxii@foxmail.com
 * @see ModelFragment
 * @see ModelResourceView
 * @see BundleParamMethodArgumentResolver
 */
public class ModelFragmentInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            Map<String, Object> model = modelAndView.getModel();
            if (model != null && model.containsKey(ModelFragment.MODEL_FRAGMENT)) {
                ((ModelFragment) model.get(ModelFragment.MODEL_FRAGMENT)).render(request);
                model.remove(ModelFragment.MODEL_FRAGMENT);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
