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

import javax.mail.Session;

/**
 * 将邮件的会话保存在配置文件或者是数据库中, 通过邮件名称(email address)再获取创建对应的mail session
 *
 * @author wuxii@foxmail.com
 */
public interface MailSession {

    Session createSession(String emailName);

}
