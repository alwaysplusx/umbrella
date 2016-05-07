package com.harmony.umbrella.mail;

import javax.mail.Session;

/**
 * 将邮件的会话保存在配置文件或者是数据库中, 通过邮件名称(email address)再获取创建对应的mail session
 *
 * @author wuxii@foxmail.com
 */
public interface MailSession {

    /**
     * 通过邮件名称加载{@linkplain javax.mail.Session}
     * 
     * @param emailName
     *            邮件名称或邮件发送人
     * @return 邮件对应的mail session
     */
    Session createSession(String emailName);

}
