package com.harmony.umbrella.security.configurers;

import com.harmony.umbrella.security.authentication.SecurityTokenAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationProviderConfigurer<B extends ProviderManagerBuilder<B>>
        extends SecurityConfigurerAdapter<AuthenticationManager, B> {

    private UserDetailsService userDetailsService;

    public SecurityTokenAuthenticationProviderConfigurer<B> userDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        return this;
    }

    @Override
    public void configure(B builder) throws Exception {
        SecurityTokenAuthenticationProvider authenticationProvider = new SecurityTokenAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        builder.authenticationProvider(authenticationProvider);
    }

}
