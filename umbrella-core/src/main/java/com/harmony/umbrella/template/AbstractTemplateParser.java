package com.harmony.umbrella.template;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author wuxii
 */
public abstract class AbstractTemplateParser implements TemplateParser {

    private ExpressionsCache expressionsCache;

    private String start = "${";
    private String end = "}";

    public AbstractTemplateParser() {
        this(NoneExpressionsCache.INSTANCE);
    }

    public AbstractTemplateParser(ExpressionsCache expressionsCache) {
        this.expressionsCache = expressionsCache;
    }

    @Override
    public final Template parse(String text) {
        Expressions expressions = expressionsCache.getExpressionsFromCache(text);
        if (expressions == null) {
            expressions = doParse(text);
        }
        return parse(expressions);
    }

    protected Expressions doParse(String text) {
        List<Expression> expressions = new ArrayList<>();

        boolean plainText = !text.startsWith(start);
        String delimiter = plainText ? start : end;
        StringTokenizer st = new StringTokenizer(text);

        while (st.hasMoreTokens()) {
            String token = st.nextToken(delimiter);
            if (token.startsWith(start)) {
                token = token.substring(start.length());
            }
            if (token.startsWith(end)) {
                token = token.substring(end.length());
            }
            expressions.add(new Expression(token, plainText));
            delimiter = (plainText = !plainText) ? start : end;
        }

        return new Expressions(text, expressions);
    }

    public void setExpressionsCache(ExpressionsCache expressionsCache) {
        this.expressionsCache = expressionsCache;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
