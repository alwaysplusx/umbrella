package com.harmony.umbrella.web.servlet.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.view.InternalResourceView;

import com.harmony.umbrella.web.method.support.ModelFragment;

/**
 * @author wuxii@foxmail.com
 * @see ModelFragment
 * @see ModelFragmentInterceptor
 */
public class ModelResourceView extends InternalResourceView {

    @Override
    protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        if (model != null && model.containsKey(ModelFragment.MODEL_FRAGMENT)) {
            ((ModelFragment) model.get(ModelFragment.MODEL_FRAGMENT)).render(request);
            model.remove(ModelFragment.MODEL_FRAGMENT);
        } else {
            super.exposeModelAsRequestAttributes(model, request);
        }
    }

}