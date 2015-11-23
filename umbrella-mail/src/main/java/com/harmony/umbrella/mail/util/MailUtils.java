/*
 * Copyright 2002-2015 the original author or authors.
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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.mail.Mail;

/**
 * @author wuxii@foxmail.com
 */
public class MailUtils {

    private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

    public static MimeMessage createMimeMessage(Mail mail, Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        // 主题
        message.setSubject(mail.getSubject());
        // 发件人
        message.setFrom(new InternetAddress(mail.getFromAddress()));
        // 收件人
        message.setRecipients(RecipientType.TO, toInternetAddresses(mail.getToAddresses()));
        // 抄送人
        if (mail.getCcAddresses() != null && !mail.getCcAddresses().isEmpty()) {
            message.setRecipients(RecipientType.CC, toInternetAddresses(mail.getCcAddresses()));
        }
        // 密送人
        if (mail.getBccAddresses() != null && !mail.getBccAddresses().isEmpty()) {
            message.setRecipients(RecipientType.BCC, toInternetAddresses(mail.getBccAddresses()));
        }

        Multipart multipart = new MimeMultipart();

        if (mail.getContent() != null) {
            addMailContent(multipart, mail.getContent(), mail.getMimeType());
        }

        if (mail.getAttachmentFiles() != null && !mail.getAttachmentFiles().isEmpty()) {
            addMailAttachment(multipart, mail.getAttachmentFiles());
        }
        message.setContent(multipart);
        message.setSentDate(new Date());
        return message;
    }

    public static void addMailContent(Multipart multipart, Object object, String mimeType) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(object, mimeType);
        multipart.addBodyPart(mimeBodyPart);
    }

    public static void addMailAttachment(Multipart multipart, List<String> resources) throws MessagingException {
        addMailAttachment(multipart, resources.toArray(new String[resources.size()]));
    }

    public static void addMailAttachment(Multipart multipart, String... resources) throws MessagingException {
        List<File> files = new ArrayList<File>(resources.length);
        for (String resource : resources) {
            File file = new File(resource);
            if (file.exists() && file.isFile()) {
                files.add(file);
            }
        }
        addMailAttachment(multipart, files.toArray(new File[files.size()]));
    }

    public static void addMailAttachment(Multipart multipart, File... files) throws MessagingException {
        for (File file : files) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            DataHandler dataHandler = new DataHandler(new FileDataSource(file));
            mimeBodyPart.setDataHandler(dataHandler);
            mimeBodyPart.setFileName(file.getName());
            multipart.addBodyPart(mimeBodyPart);
        }
    }

    private static InternetAddress[] toInternetAddresses(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return new InternetAddress[0];
        }
        List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>(addresses.size());
        for (String address : addresses) {
            try {
                internetAddresses.add(new InternetAddress(address));
            } catch (AddressException e) {
                log.warn("address " + address + " can't parse to internet address");
            }
        }
        return internetAddresses.toArray(new InternetAddress[internetAddresses.size()]);
    }

}
