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

import com.harmony.umbrella.mail.Mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author wuxii@foxmail.com
 */
public class MailUtils {

    private static final Logger log = LoggerFactory.getLogger(MailUtils.class);

    public static MimeMessage createMimeMessage(Mail mail) {
        return null;
    }

    public boolean addMailContent(Multipart multipart, String content, String contextType) {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        try {
            mimeBodyPart.setContent(content, contextType);
            multipart.addBodyPart(mimeBodyPart);
        } catch (MessagingException e) {
            log.warn("set mail content failed", e);
            return false;
        }
        return true;
    }

    public boolean addMailAttachment(Multipart multipart, File[] files) throws MessagingException {
        for (File file : files) {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            DataHandler dataHandler = new DataHandler(new FileDataSource(file));
            mimeBodyPart.setDataHandler(dataHandler);
            mimeBodyPart.setFileName(file.getName());
            multipart.addBodyPart(mimeBodyPart);
        }
        return false;
    }

}
