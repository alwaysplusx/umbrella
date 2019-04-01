package com.harmony.umbrella.web.util;

import com.harmony.umbrella.context.CurrentUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author wuxii
 */
public interface CurrentUserBuilder {

    CurrentUser build(SecurityContext securityContext, NativeWebRequest nativeWebRequest);

}
