package com.harmony.umbrella.web.util;

import com.harmony.umbrella.context.CurrentUser;
import com.harmony.umbrella.context.SimpleCurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * @author wuxii
 */
public class SimpleCurrentUserBuilder implements CurrentUserBuilder {

    @Override
    public CurrentUser build(SecurityContext securityContext, NativeWebRequest nativeWebRequest) {
        SimpleCurrentUser.Builder builder = SimpleCurrentUser.newBuilder();
        Authentication authentication = securityContext.getAuthentication();
        Object details = authentication.getDetails();
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            builder.setUsername((String) principal);
        }
        if (details instanceof UserDetails) {
            builder.setUsername(((UserDetails) details).getUsername())
                    .addProperty(UserDetails.class, details);
        }
        return builder
                .addProperty(SecurityContext.class, securityContext)
                .build();
    }

}
