package com.harmony.umbrella.mail.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import com.harmony.umbrella.mail.Mail;

/**
 * @author wuxii@foxmail.com
 */
public class MailUtils {

    /**
     * 创建基础邮件信息, 通过{@linkplain Mail}中的基础信息设置:
     * <ul>
     * <li>发件人
     * <li>收件人
     * <li>抄送人
     * <li>密送人
     * <li>邮件主题
     * </ul>
     * 
     * <b>不会在{@linkplain MimeMessage}中添加附件与正文</b>
     * 
     * @param mail
     *            邮件信息
     * @param session
     *            创建邮件的session
     * @return 邮件message
     * @throws MessagingException
     */
    public static MimeMessage createMimeMessage(Mail mail, Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        // 主题
        message.setSubject(mail.getSubject());
        // 发件人
        message.setFrom(toInternetAddresses(mail.getFromAddress()));
        // 收件人
        message.setRecipients(Message.RecipientType.TO, toInternetAddresses(mail.getToAddresses()));
        // 抄送人
        if (mail.getCcAddresses() != null && !mail.getCcAddresses().isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, toInternetAddresses(mail.getCcAddresses()));
        }
        // 密送人
        if (mail.getBccAddresses() != null && !mail.getBccAddresses().isEmpty()) {
            message.setRecipients(Message.RecipientType.BCC, toInternetAddresses(mail.getBccAddresses()));
        }
        return message;
    }

    /**
     * 设置邮件正文
     *
     * @param multipart
     *            邮件主体
     * @param content
     *            邮件正文
     * @throws MessagingException
     *             {@linkplain MimeBodyPart#setContent(Object, String)}
     * @see MimeBodyPart#setContent(Object, String)
     */
    public static void setMailContent(Multipart multipart, Object content) throws MessagingException {
        setMailContent(multipart, content, Mail.DEFAULT_MIME_TYPE);
    }

    /**
     * 设置邮件正文
     *
     * @param multipart
     *            邮件主体
     * @param content
     *            邮件正文
     * @param mimeType
     *            邮件的mimeType
     * @throws MessagingException
     *             {@linkplain MimeBodyPart#setContent(Object, String)}
     * @see MimeBodyPart#setContent(Object, String)
     */
    public static void setMailContent(Multipart multipart, Object content, String mimeType) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, mimeType);
        multipart.addBodyPart(mimeBodyPart);
    }

    /**
     * 添加邮件附件
     *
     * @param multipart
     *            邮件主体
     * @param resources
     *            资源的连接
     * @throws MessagingException
     */
    public static void addMailAttachment(Multipart multipart, String... resources) throws MessagingException {
        addMailAttachment(multipart, Arrays.asList(resources));
    }

    /**
     * 添加邮件附件
     *
     * @param multipart
     *            邮件主体
     * @param resources
     *            资源连接
     * @throws MessagingException
     */
    public static void addMailAttachment(Multipart multipart, List<String> resources) throws MessagingException {
        List<File> files = new ArrayList<File>(resources.size());
        for (String resource : resources) {
            files.add(new File(resource));
        }
        addMailAttachment(multipart, files.toArray(new File[files.size()]));
    }

    /**
     * 添加邮件附件
     *
     * @param multipart
     *            邮件主体
     * @param files
     *            附件
     * @throws MessagingException
     */
    public static void addMailAttachment(Multipart multipart, File... files) throws MessagingException {
        for (File file : files) {
            if (file.exists() && file.isFile()) {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataHandler dataHandler = new DataHandler(new FileDataSource(file));
                mimeBodyPart.setDataHandler(dataHandler);
                mimeBodyPart.setFileName(file.getName());
                multipart.addBodyPart(mimeBodyPart);
            } else {
                throw new MessagingException("attachment not exists or it's not a file");
            }
        }
    }

    /**
     * 转化为邮件地址
     * 
     * @param addresses
     *            邮件地址
     * @return {@linkplain InternetAddress}
     * @throws AddressException
     */
    public static InternetAddress[] toInternetAddresses(List<String> addresses) throws AddressException {
        if (addresses == null || addresses.isEmpty()) {
            return new InternetAddress[0];
        }
        List<InternetAddress> internetAddresses = new ArrayList<InternetAddress>(addresses.size());
        for (String address : addresses) {
            internetAddresses.add(toInternetAddresses(address));
        }
        return internetAddresses.toArray(new InternetAddress[internetAddresses.size()]);
    }

    public static InternetAddress toInternetAddresses(String address) throws AddressException {
        address = address.trim();
        int index = address.indexOf("<");
        if (index != -1 && address.endsWith(">")) {
            try {
                String emailAddress = address.substring(index + 1, address.length() - 1);
                String nickName = address.substring(0, index);
                return new InternetAddress(emailAddress, nickName);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return new InternetAddress(address);
    }
}
