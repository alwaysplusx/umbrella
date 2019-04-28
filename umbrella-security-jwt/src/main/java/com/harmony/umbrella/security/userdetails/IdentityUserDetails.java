package com.harmony.umbrella.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wuxii
 */
public interface IdentityUserDetails extends UserDetails {

    Long getUserId();

}
