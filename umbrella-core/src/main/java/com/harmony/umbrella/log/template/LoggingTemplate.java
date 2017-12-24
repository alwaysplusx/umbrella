package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Expression;
import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.util.StringUtils;

/**
 * {@linkplain Logging}解析后的产物
 * 
 * @author wuxii@foxmail.com
 */
public class LoggingTemplate {

    protected final Logging ann;

    protected MessageTokens messageTokens;

    protected List<BindToken> bindTokens;

    protected List<ScopeToken> formatedTokens;

    protected ScopeToken keyToken;

    public LoggingTemplate(Logging ann) {
        if (ann == null) {
            throw new IllegalArgumentException("logging annotation must not null");
        }
        this.ann = ann;
        this.messageTokens = new MessageTokens(ann.message());
        this.buildKeyToken(ann);
    }

    /**
     * 获取日志注解
     * 
     * @return 日志注解
     */
    public Logging getLoggingAnnotation() {
        return ann;
    }

    /**
     * 日志消息{@linkplain Logging#message()}以表达式解析后的tokens
     * 
     * @return 日志消息按表达式解析后的tokens
     */
    public MessageTokens getMessageTokens() {
        return messageTokens;
    }

    /**
     * {@linkplain Logging#message()} +
     * {@linkplain Logging#binds()}根据绑定关系计算得出的tokens
     * 
     * @return tokens
     */
    public ScopeToken[] getFormatedMessageTokens() {
        List<ScopeToken> tokens = getFormatedTokens();
        return tokens.toArray(new ScopeToken[tokens.size()]);
    }

    /**
     * 与消息内容绑定的表达式{@linkplain Logging#binds()}
     * 
     * @return bind 表达式
     */
    public ScopeToken[] getBindTokens() {
        List<BindToken> binds = getBinds();
        ScopeToken[] result = new ScopeToken[binds.size()];
        for (int i = 0, max = binds.size(); i < max; i++) {
            result[i] = binds.get(i).token;
        }
        return result;
    }

    /**
     * {@linkplain Logging#key()} ||
     * {@linkplain Logging#keyExpression()}解析后生成的key token
     * 
     * @return key token
     */
    public ScopeToken getKeyToken() {
        return keyToken;
    }

    /**
     * 通过消息message中token找寻对应binds中token
     * 
     * @param token
     *            消息中的token
     * @return bind token, if not bind retur null
     */
    public ScopeToken getBoundToken(Token token) {
        BindToken bound = getBound(token);
        return bound == null ? null : bound.token;
    }

    /**
     * 验证message的token与binds中的token是否有版定关系
     * 
     * @param token
     *            message token
     * @return true is bound, false is not
     */
    public boolean isBound(Token token) {
        return getBound(token) != null;
    }

    /**
     * 获取{@linkplain Logging#message()}绑定在{@linkplain Logging#binds()}中的token
     * 
     * @param token
     *            message 中的token
     * @return bind token
     */
    protected BindToken getBound(Token token) {
        if (token == null || token.isPlainText()) {
            return null;
        }
        for (BindToken bind : getBinds()) {
            if (bind.bind.equals(token.getToken())) {
                return bind;
            }
        }
        return null;
    }

    // 获取所有指定取值时期的待解析token
    List<ScopeToken> getAllTokens(Scope scope) {
        List<ScopeToken> result = new ArrayList<>();
        List<ScopeToken> tokens = getFormatedTokens();
        for (ScopeToken st : tokens) {
            if (!st.isPlainText() && scope.equals(st.getScope())) {
                result.add(st);
            }
        }
        if (keyToken != null && scope.equals(keyToken.getScope())) {
            result.add(keyToken);
        }
        return result;
    }

    /**
     * 解析表达式生成对应的绑定token
     * 
     * @param exp
     *            表达式
     * @return bind token
     */
    protected BindToken parseExpression(Expression exp) {
        String token = exp.text();
        if (StringUtils.isBlank(token)) {
            token = exp.value();
        }
        if (StringUtils.isBlank(token)) {
            token = exp.bind();
        }
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException(ann + " expression of " + exp + " not have any text/value/bind property");
        }
        return new BindToken(exp.bind(), token, exp.scope());
    }

    // cacheable method

    protected void buildKeyToken(Logging ann) {
        Expression[] exps = ann.keyExpression();
        if (exps.length > 0) {
            keyToken = parseExpression(exps[0]).token;
        } else if (StringUtils.isNotBlank(ann.key())) {
            keyToken = new ScopeToken(Scope.IN, ann.key());
        }
    }

    private List<BindToken> getBinds() {
        if (bindTokens == null) {
            List<BindToken> temp = new ArrayList<>();
            Expression[] binds = ann.binds();
            for (Expression exp : binds) {
                temp.add(parseExpression(exp));
            }
            bindTokens = temp;
        }
        return bindTokens;
    }

    private List<ScopeToken> getFormatedTokens() {
        if (formatedTokens == null) {
            List<Token> tokens = messageTokens.getTokens();
            List<ScopeToken> temp = new ArrayList<>(tokens.size());
            for (Token token : tokens) {
                BindToken bound = getBound(token);
                if (bound != null) {
                    temp.add(bound.token);
                } else {
                    temp.add(new ScopeToken(token.getScope(), token));
                }
            }
            formatedTokens = temp;
        }
        return formatedTokens;
    }

    protected static class BindToken {

        String bind;
        ScopeToken token;

        BindToken(String bind, String token, Scope scope) {
            this.bind = bind;
            this.token = new ScopeToken(scope, token);
        }

    }

}
