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
    // 发出地址
    protected String fromAddress;
    // 送达地址
    protected final List<String> toAddresses = new ArrayList<String>();
    // 抄送地址
    protected final List<String> ccAddresses = new ArrayList<String>();
    // 密送地址
    protected final List<String> bccAddresses = new ArrayList<String>();
    // 附件
    protected final List<String> attachmentFiles = new ArrayList<String>();
    // 邮件主题的mimeType
    protected String mimeType = DEFAULT_MIME_TYPE;

    public Mail() {
    }

    public Mail(String fromAddress, String toAddress) {

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses.clear();
        if (toAddresses != null) {
            this.toAddresses.addAll(toAddresses);
        }
    }

    public List<String> getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(List<String> ccAddresses) {
        this.ccAddresses.clear();
        if (ccAddresses != null) {
            this.ccAddresses.addAll(ccAddresses);
        }
    }

    public List<String> getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(List<String> bccAddresses) {
        this.bccAddresses.clear();
        if (bccAddresses != null) {
            this.bccAddresses.addAll(bccAddresses);
        }
    }

    public List<String> getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(List<String> attachmentFiles) {
        this.attachmentFiles.clear();
        if (attachmentFiles != null) {
            this.attachmentFiles.addAll(attachmentFiles);
        }
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

}
