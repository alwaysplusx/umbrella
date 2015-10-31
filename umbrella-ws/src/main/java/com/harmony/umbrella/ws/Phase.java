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
package com.harmony.umbrella.ws;

/**
 * 服务交互的执行周期
 * 
 * @author wuxii@foxmail.com
 */
public enum Phase {

    /**
     * 执行前
     */
    PRE_INVOKE {

        @Override
        public String CXFPhase() {
            return org.apache.cxf.phase.Phase.POST_UNMARSHAL;
        }
    },
    /**
     * 有{@linkplain WebServiceAbortException} 取消执行
     */
    ABORT {

        @Override
        public String CXFPhase() {
            return null;
        }
    },
    /**
     * 执行成功后
     */
    POST_INVOKE {

        @Override
        public String CXFPhase() {
            return org.apache.cxf.phase.Phase.PRE_MARSHAL;
        }
    },
    /**
     * 执行异常
     */
    THROWING {

        @Override
        public String CXFPhase() {
            return null;
        }
    },
    /**
     * 交互的finally块中
     */
    FINALLY {

        @Override
        public String CXFPhase() {
            return null;
        }

    };

    /**
     * for CXF {@linkplain org.apache.cxf.phase.Phase}
     * 
     * @return cxf phase
     */
    public abstract String CXFPhase();

}
