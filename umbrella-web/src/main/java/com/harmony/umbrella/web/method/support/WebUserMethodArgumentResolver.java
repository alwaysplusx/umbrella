package com.harmony.umbrella.web.method.support;

import com.harmony.umbrella.context.CurrentUser;
import com.harmony.umbrella.web.method.annotation.WebUser;
import com.harmony.umbrella.web.method.bind.MissingWebUserException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author wuxii
 */
public class WebUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WebUser.class)
                || parameter.getParameterType() == CurrentUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        WebUser ann = parameter.getParameterAnnotation(WebUser.class);
        CurrentUser currentUser = getCurrentUser(webRequest);
        if (currentUser == null && ann != null && ann.required()) {
            throw new MissingWebUserException("request not exists current user");
        }
        return currentUser;
    }

    protected CurrentUser getCurrentUser(NativeWebRequest webRequest) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null
                || context.getAuthentication() == null
                || context.getAuthentication().getPrincipal() == null) {
            return null;
        }
        String username = null;
        Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        return new CurrentUser(null, username, null);
    }

}
