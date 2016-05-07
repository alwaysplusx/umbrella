package com.harmony.umbrella.mail.util;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
    public void test() throws MessagingException, IOException {
        Mail mail = new Mail("关羽<tomeejee@126.com>", "wuxii<870757543@qq.com>", "Come On", "I Support You");
        MimeMessage message = MailUtils.createMimeMessage(mail, session);
        Multipart multipart = new MimeMultipart();

        mail.addAttachment("pom.xml");
        mail.addAttachment("umbrella-mail.iml");

        if (mail.hasAttachment()) {
            MailUtils.addMailAttachment(multipart, mail.getAttachmentFiles());
        }

        MailUtils.setMailContent(multipart, mail.getContent(), mail.getMimeType());
        message.setContent(multipart);
        send(message);
    }

    private static void send(Message message) throws MessagingException {
        Transport.send(message);
    }

}
