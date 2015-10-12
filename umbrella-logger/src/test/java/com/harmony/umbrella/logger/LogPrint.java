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
package com.harmony.umbrella.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author wuxii@foxmail.com
 */
public class LogPrint {

    // private static final Logger log =
    // LoggerFactory.getLogger(LogPrint.class);

    public static void main(String[] args) {

        Logger log = LogManager.getLogger(LogPrint.class);

        log.info(">>>>>>>>>>>>>> log");

        log.debug(">>>>>>>>>>>> debug");

        // new TestLogger().print();

    }

}
