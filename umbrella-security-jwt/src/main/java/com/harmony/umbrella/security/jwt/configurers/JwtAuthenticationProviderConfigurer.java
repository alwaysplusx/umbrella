package com.harmony.umbrella.security.jwt.configurers;

import com.harmony.umbrella.security.jwt.security.JwtAuthenticationProvider;
import com.harmony.umbrella.security.jwt.JwtUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;

/**
 * @author wuxii
 */
public class JwtAuthenticationProviderConfigurer<B extends ProviderManagerBuilder<B>>
        extends SecurityConfigurerAdapter<AuthenticationManager, B> {

    private JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthenticationProviderConfigurer<B> jwtUserDetailsService(JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        return this;
    }

    @Override
    public void configure(B builder) {
        JwtAuthenticationProvider provider = postProcess(new JwtAuthenticationProvider(jwtUserDetailsService));
        builder.authenticationProvider(provider);
    }

}
