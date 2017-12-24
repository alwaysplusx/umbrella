package com.harmony.umbrella.log;

import java.util.List;

import org.junit.Test;

import com.harmony.umbrella.log.template.MessageTokens;
import com.harmony.umbrella.log.template.Token;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTokensTest {

    @Test
    public void test() {
        MessageTokens mts = new MessageTokens("用户[{$.user.username}]对日志进行打印操作, 打印的日志信息为[{$.model}]");
        List<Token> tokens = mts.getTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

}
