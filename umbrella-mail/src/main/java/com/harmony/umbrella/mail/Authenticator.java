package com.harmony.umbrella.mail;

import javax.mail.PasswordAuthentication;

/**
 * 邮件身份验证
 * 
 * @author wuxii@foxmail.com
 */
public class Authenticator extends javax.mail.Authenticator {

    private String username;
    private String password;

    public Authenticator() {
    }

    public Authenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}
