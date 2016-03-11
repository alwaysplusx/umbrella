/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrinter {

    private static final Log log = Logs.getLog(LogPrinter.class);

    static String message;

    static {
    }

    public static void main(String[] args) throws InterruptedException {

        LogMessage msg = LogMessage.create(log)//
                .bizId(1L)//
                .start();

        Thread.sleep(1000);

        msg.finish().bizModule("Object")//
                .operator("wuxii")//
                .operatorId("1")//
                .action("保存")//
                .module("Sample")//
                .level(Level.INFO)//
                .currentStack()//
                .currentThread()//
                .message("some text").log();
    }

}
