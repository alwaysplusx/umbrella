package com.harmony.umbrella.xml;

import java.util.ArrayDeque;

/**
 * @author wuxii@foxmail.com
 */
public class HandleStack extends ArrayDeque<String> {

    private static final long serialVersionUID = 1L;

    public int countName(String name) {
        int count = 0;
        for (String n : this) {
            if (n.equalsIgnoreCase(name)) {
                count++;
            }
        }
        return count;
    }

}
