package com.harmony.umbrella.log.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Scope;

/**
 * 日志message的独立节点token. {@linkplain Logging#message() 一段文本}可被解析为多个token.
 * 如果token满足表达式条件则{@linkplain #iterator()}返回的为按切割符切割后的各个节点的节点
 * <p>
 * logging & expression:
 * 
 * <pre>
 * 
 *   &#64;Logging(message = "save user {target.user.username} to some place.")
 *
 *            token          |          iterator
 *  -------------------------|-------------------------------
 *   'save user '            | ['save user ']
 *   'target.user.username'  | ['target', 'user', 'username']
 *   ' to some place.'       | [' to some place.']
 * 
 * </pre>
 * 
 * @author wuxii@foxmail.com
 */
public class Token implements Iterable<String> {

    /**
     * token原文本
     */
    protected final String token;
    /**
     * 表达式的切割符
     */
    protected final String delim;

    /**
     * 该token是否为纯文本的标志
     */
    protected final boolean plainText;
    /**
     * token相关的关键字
     */
    private final KeyWord keyWord;
    /**
     * 切割后的items
     */
    private List<String> items;

    Token(String token) {
        this(token, true);
    }

    Token(String token, boolean plainText) {
        this(token, ".", plainText);
    }

    Token(String token, String delim, boolean plainText) {
        this.token = token;
        this.delim = delim;
        this.plainText = plainText;
        this.keyWord = findKeyWord(token);
    }

    public KeyWord getKeyWord() {
        return keyWord;
    }

    public String getToken() {
        return token;
    }

    public boolean isPlainText() {
        return plainText;
    }

    @Override
    public Iterator<String> iterator() {
        return getItems().iterator();
    }

    public Iterator<String> iterator(String delim) {
        return getItems(delim).iterator();
    }

    /**
     * 获取节点下切割后的节点item集合
     * 
     * @return token items
     */
    public List<String> getItems() {
        if (items == null) {
            items = getItems(delim);
        }
        return Collections.unmodifiableList(items);
    }

    /**
     * 获取按特定切割符切割后的节点item集合
     * 
     * @param delimiter
     *            切割负
     * @return token items
     */
    public List<String> getItems(String delimiter) {
        List<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(token, delimiter);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken().trim());
        }
        return result;
    }

    Scope getScope() {
        return plainText || keyWord == null || keyWord.scopes() == null || keyWord.scopes().size() == 0 ? //
                null : //
                keyWord.scopes().iterator().next();
    }

    @Override
    public String toString() {
        return token;
    }

    public static KeyWord findKeyWord(String token) {
        if (token == null) {
            return null;
        }
        for (KeyWord kw : KeyWords.allKeyWords) {
            for (String alias : kw.alias()) {
                if (token.startsWith(alias)) {
                    return kw;
                }
            }
        }
        return null;
    }
}