package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.detector.MemberDetectors;

/**
 * @author wuxii@foxmail.com
 */
public class TokenResolvers implements TokenResolver {

    private List<TokenResolver> tokenResolvers = new ArrayList<>();

    public TokenResolvers() {
    }

    public TokenResolvers(TokenResolver... tokenResolvers) {
        this(Arrays.asList(tokenResolvers));
    }

    public TokenResolvers(List<TokenResolver> tokenResolvers) {
        this.tokenResolvers = new ArrayList<>(tokenResolvers);
    }

    @Override
    public boolean support(ScopeToken token) {
        return getTokenResolver(token) != null;
    }

    @Override
    public Object resolve(ScopeToken token, LoggingContext loggingContext) {
        TokenResolver resolver = getTokenResolver(token);
        if (resolver == null) {
            throw new LoggingException("unsupported scope tooken " + token);
        }
        return resolver.resolve(token, loggingContext);
    }

    protected TokenResolver getTokenResolver(ScopeToken token) {
        for (TokenResolver tr : tokenResolvers) {
            if (tr.support(token)) {
                return tr;
            }
        }
        return null;
    }

    public List<TokenResolver> getTokenResolvers() {
        return tokenResolvers;
    }

    public void setTokenResolvers(List<TokenResolver> tokenResolvers) {
        this.tokenResolvers = tokenResolvers;
    }

    public static TokenResolvers defaultTokenResolvers() {
        return new TokenResolvers(new KeyWordTokenResolver(MemberDetectors.allDetectors()));
    }

}
