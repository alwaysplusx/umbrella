package com.harmony.umbrella.log4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractAppender extends AppenderSkeleton {

    private boolean ignoreExceptions = true;

    // 根据配置生成的信息
    private boolean initialize;

    @Override
    protected void append(LoggingEvent event) {
        // 只有自定义的日志消息才进入数据库，自定义类型为logInfo
        if (event.getMessage() instanceof LogInfo) {
            _init();
            try {
                append((LogInfo) event.getMessage());
            } catch (Throwable e) {
                if (ignoreExceptions) {
                    return;
                }
                ReflectionUtils.rethrowRuntimeException(e);
            }
        }
    }

    protected void _init() {
        if (!initialize) {
            synchronized (getClass()) {
                if (!initialize) {
                    init();
                    this.initialize = true;
                }
            }
        }
    }

    protected abstract void init();

    protected abstract void append(LogInfo logInfo);

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }

    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

}
