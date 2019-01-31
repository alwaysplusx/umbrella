package com.harmony.umbrella.security.jwt.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.harmony.umbrella.security.jwt.JwtDecodeException;
import com.harmony.umbrella.security.jwt.JwtToken;
import com.harmony.umbrella.security.jwt.JwtTokenHandler;
import com.harmony.umbrella.security.userdetails.IdentityUserDetails;
import com.harmony.umbrella.util.TimeUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author wuxii
 */
public class Auth0JwtTokenHandler implements JwtTokenHandler {

    private int expiresIn = 7200;

    private String issuer;

    private final String signature;

    public Auth0JwtTokenHandler(String signature) {
        Assert.notNull(signature, "jwt token signature not nullable");
        this.signature = signature;
    }

    @Override
    public String generate(UserDetails userDetails, HttpServletRequest request) {
        String username = userDetails.getUsername();
        Date now = new Date();
        Date expiresTime = TimeUtils.addSeconds(now, expiresIn);
        JWTCreator.Builder builder = JWT.create();
        if (StringUtils.hasText(issuer)) {
            builder.withIssuer(issuer);
        }
        if (userDetails instanceof IdentityUserDetails) {
            builder.withClaim("uid", ((IdentityUserDetails) userDetails).getUserId());
        }
        return builder
                .withAudience(username)
                .withIssuedAt(new Date())
                .withExpiresAt(expiresTime)
                .withNotBefore(now)
                .sign(Algorithm.HMAC512(signature));
    }

    @Override
    public JwtToken decode(String token) throws JwtDecodeException {
        DecodedJWT jwt;
        try {
            jwt = JWT
                    .require(Algorithm.HMAC512(signature))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new JwtDecodeException("jwt verification failed", e);
        }
        return new JwtToken(jwt);
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

}
