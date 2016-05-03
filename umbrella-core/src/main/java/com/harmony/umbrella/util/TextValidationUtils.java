package com.harmony.umbrella.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wuxii@foxmail.com
 */
public class TextValidationUtils {

    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static final String REGEX_PHONENUMBER = "^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";

    public static boolean isEmail(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        Pattern regex = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

    public static boolean isPhoneNumber(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        Pattern regex = Pattern.compile(REGEX_PHONENUMBER);
        Matcher matcher = regex.matcher(text);
        return matcher.matches();
    }

}
