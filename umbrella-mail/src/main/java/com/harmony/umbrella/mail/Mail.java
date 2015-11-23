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

import java.io.File;

/**
 * @author wuxii@foxmail.com
 */
public class Mail {

    // 发出地址
    protected String fromAddress;
    // 送达地址
    protected String[] toAddresses;
    // 抄送地址
    protected String[] ccAddresses;
    // 密送地址
    protected String[] bccAddresses;
    // 邮件主题
    protected String subject;
    // 邮件内容
    protected Object content;
    // 附件
    protected File[] attachmentFiles;

    public File[] getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(File[] attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }

    public String[] getBccAddresses() {
        return bccAddresses;
    }

    public void setBccAddresses(String[] bccAddresses) {
        this.bccAddresses = bccAddresses;
    }

    public String[] getCcAddresses() {
        return ccAddresses;
    }

    public void setCcAddresses(String[] ccAddresses) {
        this.ccAddresses = ccAddresses;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String[] getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String[] toAddresses) {
        this.toAddresses = toAddresses;
    }

}
