package com.harmony.umbrella.security.jwt.support;

import com.harmony.umbrella.security.jwt.JwtTokenException;
import com.harmony.umbrella.security.jwt.JwtTokenGenerator;
import com.harmony.umbrella.security.jwt.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wuxii
 */
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private JwtTokenGenerator jwtTokenGenerator;

    private TokenResponseRender tokenResponseRender = new AjaxTokenResponseRender();

    public JwtAuthenticationSuccessHandler() {
    }

    public JwtAuthenticationSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof JwtUserDetails)) {
            throw new JwtTokenException("unsupported authentication type " + authentication);
        }

        String tokenValue = jwtTokenGenerator.generate((JwtUserDetails) principal, request);
        tokenResponseRender.render(tokenValue, request, response);
    }

    public void setJwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    public void setTokenResponseRender(TokenResponseRender tokenResponseRender) {
        this.tokenResponseRender = tokenResponseRender;
    }
}
