package com.harmony.umbrella.mail;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class Mail {

    public static final String DEFAULT_MIME_TYPE = "text/html; charset=utf-8";
    // 邮件主题
    protected String subject;
    // 邮件内容
    protected Object content;
    // 邮件的模版名称
    protected String templateName;
    // 发出地址
    protected String fromAddress;
    // 送达地址
    protected List<String> toAddresses = new ArrayList<String>();
    // 抄送地址
    protected List<String> ccAddresses = new ArrayList<String>();
    // 密送地址
    protected List<String> bccAddresses = new ArrayList<String>();
    // 附件
    protected List<String> attachmentFiles = new ArrayList<String>();
    // 邮件主题的mimeType
    protected String mimeType = DEFAULT_MIME_TYPE;

    public Mail() {
    }

    public Mail(String fromAddress, String toAddress) {
        this(fromAddress, toAddress, null, null);
    }

    public Mail(String fromAddress, String toAddress, String subject) {
        this(fromAddress, toAddress, subject, null);
    }

    public Mail(String fromAddress, String toAddress, String subject, String content) {
        this.fromAddress = fromAddress;
        this.toAddresses.add(toAddress);
        this.subject = subject;
        this.content = content;
    }

    /**
     * 添加附件
     * 
     * @param attachment
     *            附件
     */
    public void addAttachment(String attachment) {
        getAttachmentFiles().add(attachment);
    }

    /**
     * 检测邮件中是否包含附件
     * 
     * @return true 邮件含有附件
     */
    public boolean hasAttachment() {
        return attachmentFiles != null && !attachmentFiles.isEmpty();
    }

    /**
     * 邮件附件列表
     * 
     * @return 邮件附件列表
     */
    public List<String> getAttachmentFiles() {
        return attachmentFiles;
    }

    /**
     * 设置邮件附件
     * 
     * @param attachmentFiles
     *            附件
     */
    public void setAttachmentFiles(List<String> attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }

    /**
     * 收件人列表, never return null
     * 
     * @return 送达地址
     */
    public List<String> getToAddresses() {
        if (toAddresses == null) {
            toAddresses = new ArrayList<String>();
        }
        return toAddresses;
    }

    /**
     * 设置收件人
     * 
     * @param toAddresses
     *            收件人
     */
    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    /**
     * 抄送人列表
     * 
     * @return 抄送人列表
     */
    public List<String> getBccAddresses() {
        if (bccAddresses == null) {
            bccAddresses = new ArrayList<String>();
        }
        return bccAddresses;
    }

    /**
     * 设置抄送人
     * 
     * @param bccAddresses
     *            抄送人
     */
    public void setBccAddresses(List<String> bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    /**
     * 密送人列表
     * 
     * @return 密送人列表
     */
    public List<String> getCcAddresses() {
        if (ccAddresses == null) {
            ccAddresses = new ArrayList<String>();
        }
        return ccAddresses;
    }

    /**
     * 设置密送人
     * 
     * @param ccAddresses
     *            密送人
     */
    public void setCcAddresses(List<String> ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * 邮件正文
     * 
     * @return 邮件正文
     */
    public Object getContent() {
        return content;
    }

    /**
     * 设置邮件正文
     * 
     * @param content
     *            邮件正文
     */
    public void setContent(Object content) {
        setContent(content, null);
    }

    /**
     * 设置邮件正文, 设置模版名称不为null则标识邮件的正文由模版加载而来, 解析正文时候通过向模版中添加正文数据生成真正的邮件正文
     * 
     * @param content
     *            邮件正文
     * @param templateName
     *            邮件的模版
     */
    public void setContent(Object content, String templateName) {
        this.content = content;
        this.templateName = templateName;
    }

    /**
     * 检测邮件是否是模版邮件
     * 
     * @return true正文由模版生成
     */
    public boolean isTemplateMail() {
        return templateName != null;
    }

    /**
     * 邮件发件人
     * 
     * @return 发件人
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * 设置发件人
     * 
     * @param fromAddress
     *            设置发件人
     */
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    /**
     * 邮件正文的mimeType
     * 
     * @return 邮件正文的mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * 设置邮件正文的mimeType
     * 
     * @param mimeType
     *            邮件正文的mimeType
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 邮件主题
     * 
     * @return 邮件主题
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置邮件主题
     * 
     * @param subject
     *            邮件主题
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

}
