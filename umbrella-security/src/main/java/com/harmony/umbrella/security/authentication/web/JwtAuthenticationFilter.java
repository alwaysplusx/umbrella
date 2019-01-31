package com.harmony.umbrella.security.authentication.web;

import com.harmony.umbrella.security.JwtTokenDecoder;
import com.harmony.umbrella.security.JwtTokenExtractor;
import com.harmony.umbrella.security.authentication.JwtAuthenticationToken;
import com.harmony.umbrella.util.StringUtils;
import org.springframework.core.OrderComparator;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class JwtAuthenticationFilter extends AbstractTokenAuthenticationFilter {

    private List<JwtTokenExtractor> jwtTokenExtractors = new ArrayList<>();

    private JwtTokenDecoder jwtTokenDecoder;

    @Override
    protected Authentication getRequestAuthentication(HttpServletRequest request) {
        String tokenValue = null;
        for (JwtTokenExtractor extractor : jwtTokenExtractors) {
            tokenValue = extractor.extract(request);
            if (StringUtils.hasText(tokenValue)) {
                break;
            }
        }
        if (!StringUtils.hasText(tokenValue)) {
            return null;
        }
        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(jwtTokenDecoder.decode(tokenValue));
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return authRequest;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        OrderComparator.sort(jwtTokenExtractors);
    }

    public void setJwtTokenExtractors(List<JwtTokenExtractor> jwtTokenExtractors) {
        this.jwtTokenExtractors = jwtTokenExtractors;
    }

    public void setJwtTokenDecoder(JwtTokenDecoder jwtTokenDecoder) {
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

}