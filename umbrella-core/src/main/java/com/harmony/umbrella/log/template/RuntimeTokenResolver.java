package com.harmony.umbrella.log.template;

import java.util.Map;

import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.log.template.LoggingContext.ValueContext;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class RuntimeTokenResolver implements TokenResolver {

    @Override
    public boolean support(ScopeToken scopeToken) {
        KeyWord kw = scopeToken.getKeyWord();
        String token = scopeToken.getTokenString().trim();
        if (kw != null || StringUtils.isBlank(token)) {
            return false;
        }
        for (int i = 0, max = token.length(); i < max; i++) {
            char c = token.charAt(i);
            if (!Character.isUpperCase(c) || c != '_') {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object resolve(ScopeToken scopeToken, LoggingContext context) {
        String token = scopeToken.getTokenString().trim();
        Scope scope = scopeToken.getScope();
        ValueContext valueContext = context.getValueContext();
        Map<String, Object> ctx = !Scope.IN.equals(scope) ? valueContext.getOutContext() : valueContext.getInContext();
        return ctx.get(token);
    }

}
