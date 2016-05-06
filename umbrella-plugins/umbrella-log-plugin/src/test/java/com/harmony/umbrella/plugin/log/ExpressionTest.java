package com.harmony.umbrella.plugin.log;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionTest {

    private static final char escape = '\\';

    public static void main(String[] args) {
        String e = "通过注入数据{0.name}, 将数据提交给后台{target}. 保存结果:\\{{result}\\}";
        for (int i = 0; i < e.length(); i++) {
            if (e.charAt(i) == escape && i + 1 < e.length() && isDelimer(e.charAt(i + 1))) {
                i++;
            }
            System.out.print(e.charAt(i) + "");
        }
    }

    private static boolean isDelimer(char c) {
        return c == '{' || c == '}';
    }

}
