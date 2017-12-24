package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTokens implements Iterable<Token> {

    /**
     * 未格式化的日志文本
     */
    protected final String message;
    protected final String start;
    protected final String end;

    private List<Token> tokens;

    public MessageTokens(String message) {
        this(message, '{', '}');
    }

    public MessageTokens(String message, char start, char end) {
        this.message = message;
        this.start = start + "";
        this.end = end + "";
    }

    @Override
    public Iterator<Token> iterator() {
        return getTokens().iterator();
    }

    public List<Token> getTokens() {
        if (tokens == null) {
            tokens = parseText();
        }
        return Collections.unmodifiableList(tokens);
    }

    protected List<Token> parseText() {
        List<Token> result = new ArrayList<>();

        boolean plainText = !message.startsWith(start);
        StringTokenizer st = new StringTokenizer(message);
        String delim = plainText ? start : end;

        while (st.hasMoreTokens()) {
            String token = st.nextToken(delim);
            if (token.startsWith(start) || token.startsWith(end)) {
                // delim always char
                token = token.substring(1);
            }
            result.add(new Token(token, plainText));
            delim = (plainText = !plainText) ? start : end;
        }

        return result;
    }

}
