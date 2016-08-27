package com.harmony.umbrella.log.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public abstract class BufferedLogInfoManager implements LogInfoManager {

    protected final List<LogInfo> buffer;
    protected final int bufferSize;

    public BufferedLogInfoManager(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new ArrayList<LogInfo>(bufferSize + 1);
    }

    /**
     * 提交关闭连接
     */
    protected abstract void commitAndClose();

    /**
     * 开始并获取连接
     */
    protected abstract void connectAndStart();

    /**
     * 写日志
     * 
     * @param logInfo
     *            日志
     * @param event
     *            日志源事件
     */
    protected abstract void writeInternal(LogInfo logInfo);

    @Override
    public void write(LogInfo logInfo) {
        synchronized (this.getClass()) {
            if (this.bufferSize > 0) {
                this.buffer.add(logInfo);
                if (this.buffer.size() >= this.bufferSize) {
                    this.flush();
                }
            } else {
                this.connectAndStart();
                try {
                    this.writeInternal(logInfo);
                } finally {
                    this.commitAndClose();
                }
            }
        }
    }

    public void flush() {
        if (this.buffer.size() > 0) {
            this.connectAndStart();
            try {
                for (LogInfo logInfo : this.buffer) {
                    this.writeInternal(logInfo);
                }
            } finally {
                this.commitAndClose();
                // not sure if this should be done when writing the events failed
                this.buffer.clear();
            }
        }
    }

    protected static Object lookup(String jndi, Properties properties) {
        try {
            return new InitialContext(properties).lookup(jndi);
        } catch (NamingException e) {
        }
        return null;
    }

}
