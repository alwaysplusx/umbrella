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
package com.harmony.umbrella.jaxws.phase;

import static com.harmony.umbrella.jaxws.Phase.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.jaxws.Handler;
import com.harmony.umbrella.jaxws.Handler.HandleMethod;
import com.harmony.umbrella.jaxws.JaxWsAbortException;
import com.harmony.umbrella.jaxws.Phase;
import com.harmony.umbrella.jaxws.services.HelloService;

/**
 * @author wuxii@foxmail.com
 */
@Handler(HelloService.class)
public class HelloServiceSayHiPhaseValidation {

    private static final Logger log = LoggerFactory.getLogger(HelloServiceSayHiPhaseValidation.class);

    @HandleMethod(phase = Phase.PRE_INVOKE)
    public boolean sayHi(String message, Map<String, Object> content) {
        log.info("hello phase[{}] validation, message {}, content {}", PRE_INVOKE, message, content);
        return true;
    }

    @HandleMethod(phase = Phase.ABORT)
    public void sayHi(JaxWsAbortException exception, String message, Map<String, Object> content) {
        log.info("hello phase[{}] validation, message {}, content {}", ABORT, message, content);
    }

    @HandleMethod(phase = Phase.POST_INVOKE)
    public void sayHi(String result, String message, Map<String, Object> content) {
        log.info("hello phase[{}] validation, message {}, content {}", POST_INVOKE, message, content);
    }

    @HandleMethod(phase = Phase.THROWING)
    public void sayHi(Throwable e, String message, Map<String, Object> content) {
        log.info("hello phase[{}] validation, message {}, content {}", THROWING, message, content);
    }

    @HandleMethod(phase = Phase.FINALLY)
    public void sayHi(Throwable e, String result, String message, Map<String, Object> content) {
        log.info("hello phase[{}] validation, message {}, content {}", FINALLY, message, content);
    }
}
