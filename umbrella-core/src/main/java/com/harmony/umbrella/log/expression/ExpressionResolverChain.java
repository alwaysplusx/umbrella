package com.harmony.umbrella.log.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionResolverChain {

    private List<ExpressionResolver> resolvers = new ArrayList<ExpressionResolver>();

    protected static final List<ExpressionResolver> DEFAULT_RESOLVERS;

    static {
        ArrayList<ExpressionResolver> list = new ArrayList<ExpressionResolver>();
        list.add(HttpExpressionResolver.INSTANCE);
        list.add(ArrayExpressionResolver.INSTANCE);
        list.add(MapExpressionResolver.INSTANCE);
        list.add(MethodExpressionResolver.INSTANCE);
        list.add(NumberExpressionResolver.INSTANCE);
        list.add(NamedExpressionResolver.INSTANCE);
        DEFAULT_RESOLVERS = Collections.unmodifiableList(list);
    }

    protected ExpressionResolverChain(Collection<ExpressionResolver> resolvers) {
        this.resolvers.addAll(resolvers);
    }

    public Object doChain(String expressionText, Object value) {
        Object result = value;
        // split expressionText by dot
        StringTokenizer st = new StringTokenizer(expressionText, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Iterator<ExpressionResolver> iterator = updateIterator();
            while (iterator.hasNext()) {
                ExpressionResolver resolver = iterator.next();
                if (resolver.support(token, result)) {
                    result = resolver.resolve(token, result);
                    break;
                }
            }
        }
        return result;
    }

    protected Iterator<ExpressionResolver> updateIterator() {
        Collections.sort(resolvers);
        return resolvers.iterator();
    }

    public static ExpressionResolverChain defaultChain() {
        return new ExpressionResolverChain(DEFAULT_RESOLVERS);
    }

    public static ExpressionResolverChain createChain(Collection<ExpressionResolver> resolvers) {
        if (resolvers.isEmpty()) {
            return defaultChain();
        }
        List<ExpressionResolver> list = new ArrayList<ExpressionResolver>(resolvers.size() + DEFAULT_RESOLVERS.size());
        list.addAll(resolvers);
        list.addAll(DEFAULT_RESOLVERS);
        return new ExpressionResolverChain(list);
    }

    public static ExpressionResolverChain createChain(Logging ann) throws Exception {
        if (ann.resolver().length == 0) {
            return defaultChain();
        }
        List<ExpressionResolver> resolvers = new ArrayList<ExpressionResolver>(DEFAULT_RESOLVERS.size() + ann.resolver().length);
        resolvers.addAll(DEFAULT_RESOLVERS);
        for (Class<? extends ExpressionResolver> resolverClass : ann.resolver()) {
            resolvers.add(resolverClass.newInstance());
        }
        return new ExpressionResolverChain(resolvers);
    }

}
