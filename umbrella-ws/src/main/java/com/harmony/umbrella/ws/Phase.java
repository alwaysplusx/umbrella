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
