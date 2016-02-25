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
package com.harmony.umbrella.ws.ext;

import com.harmony.modules.commons.log.Log4jUtils;
import com.harmony.umbrella.monitor.ext.LogUtils;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.cxf.log.LogMessage;
import com.harmony.umbrella.ws.cxf.log.LogMessageHandler;

/**
 * cxf的soap消息监控工具类
 * 
 * @author wuxii@foxmail.com
 */
class HarmonyLogMessageHandler implements LogMessageHandler {

    @Override
    public void handle(LogMessage logMessage) {

        String model = logMessage.getType();

        String result = logMessage.isException() ? "异常" : "正常";

        // 需要在配置文件umbrella.properties中配置
        String fromName = logMessage.getOperationName();
        fromName = StringUtils.isNotBlank(fromName) ? LogUtils.getLogFromName(fromName) : "unknow";

        String message = LogUtils.format(model, result, fromName, "SP-1000000", logMessage.toString());

        Throwable ex = logMessage.getException();

        System.out.println(message);

        if (ex == null) {
            Log4jUtils.logSysInfo(message, null);
        } else {
            Log4jUtils.logSysError(message, ex);
        }

    }
}
