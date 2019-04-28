package com.harmony.umbrella.security.configurers;

import com.harmony.umbrella.security.authentication.SecurityTokenAuthenticationProvider;
import com.harmony.umbrella.security.userdetails.SecurityTokenUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationProviderConfigurer<B extends ProviderManagerBuilder<B>>
        extends SecurityConfigurerAdapter<AuthenticationManager, B> {

    private SecurityTokenUserDetailsService securityTokenUserDetailsService;

    public SecurityTokenAuthenticationProviderConfigurer<B> securityTokenUserDetailsService(SecurityTokenUserDetailsService securityTokenUserDetailsService) {
        this.securityTokenUserDetailsService = securityTokenUserDetailsService;
        return this;
    }

    @Override
    public void configure(B builder) throws Exception {
        SecurityTokenAuthenticationProvider authenticationProvider = new SecurityTokenAuthenticationProvider();
        authenticationProvider.setSecurityTokenUserDetailsService(securityTokenUserDetailsService);
        builder.authenticationProvider(authenticationProvider);
    }

}
