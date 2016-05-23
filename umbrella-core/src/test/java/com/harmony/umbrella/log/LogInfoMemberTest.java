package com.harmony.umbrella.log;

import java.lang.reflect.Method;

import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LogInfoMemberTest {

    public static void main(String[] args) {
        Method[] methods = LogInfo.class.getMethods();

        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) && ReflectionUtils.isReadMethod(method)) {
                System.out.println(method.getName().substring(3));
            }
        }
    }

}
