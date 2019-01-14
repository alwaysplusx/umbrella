package com.harmony.umbrella.web.servlet.view;

import com.harmony.umbrella.web.method.support.ModelFragment;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 * @see ModelFragment
 * @see com.harmony.umbrella.web.servlet.handler.ModelFragmentInterceptor
 */
public class ModelResourceView extends InternalResourceView {

    @Override
    protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
        if (model.containsKey(ModelFragment.MODEL_FRAGMENT)) {
            ((ModelFragment) model.get(ModelFragment.MODEL_FRAGMENT)).render(request);
            model.remove(ModelFragment.MODEL_FRAGMENT);
        } else {
            super.exposeModelAsRequestAttributes(model, request);
        }
    }

}