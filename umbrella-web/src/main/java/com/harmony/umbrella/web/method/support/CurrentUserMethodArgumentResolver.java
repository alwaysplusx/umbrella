package com.harmony.umbrella.web.method.support;

import com.harmony.umbrella.context.CurrentUser;
import com.harmony.umbrella.web.method.annotation.WebUser;
import com.harmony.umbrella.web.method.bind.MissingCurrentUserException;
import com.harmony.umbrella.web.util.CurrentUserBuilder;
import com.harmony.umbrella.web.util.SimpleCurrentUserBuilder;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author wuxii
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private CurrentUserBuilder currentUserBuilder;

    public CurrentUserMethodArgumentResolver() {
        this(new SimpleCurrentUserBuilder());
    }

    public CurrentUserMethodArgumentResolver(CurrentUserBuilder currentUserBuilder) {
        this.currentUserBuilder = currentUserBuilder;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType() == CurrentUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        CurrentUser currentUser = getCurrentUser(webRequest);
        if (currentUser == null) {
            throw new MissingCurrentUserException("request not exists current user");
        }
        WebUser ann = parameter.getParameterAnnotation(WebUser.class);
        if (ann != null && !ann.allowAnonymous() && currentUser.isAnonymous()) {
            throw new MissingCurrentUserException("request not allow binding anonymous user");
        }
        return currentUser;
    }

    protected CurrentUser getCurrentUser(NativeWebRequest webRequest) {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null
                || context.getAuthentication() == null
                || context.getAuthentication().getPrincipal() == null) {
            return CurrentUser.anonymous();
        }
        return currentUserBuilder.build(context, webRequest);
    }

    public void setCurrentUserBuilder(CurrentUserBuilder currentUserBuilder) {
        this.currentUserBuilder = currentUserBuilder;
    }

}
