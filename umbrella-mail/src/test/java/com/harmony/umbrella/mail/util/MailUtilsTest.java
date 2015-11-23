/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.mail.util;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import com.harmony.umbrella.mail.Authenticator;
import com.harmony.umbrella.mail.Mail;

/**
 * @author wuxii@foxmail.com
 */
public class MailUtilsTest {

    private static final Session session;

    static {
        Authenticator auth = new Authenticator("tomeejee@126.com", "abc123");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.126.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        session = Session.getDefaultInstance(props, auth);
    }

    @Test
    public void test() throws MessagingException {
        Mail mail = new Mail("tomeejee@126.com", "870757543@qq.com", "Come On", "I Support You");
        MimeMessage message = MailUtils.createMimeMessage(mail, session);
        System.out.println(message);
    }

}
