package com.harmony.umbrella.log.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBufferedLogWriter implements LogWriter {

    private final CopyOnWriteArrayList<LogInfo> buffer;
    protected final int bufferSize;
    protected final boolean failSafe;

    public AbstractBufferedLogWriter(int bufferSize) {
        this(bufferSize, true);
    }

    public AbstractBufferedLogWriter(int bufferSize, boolean failSafe) {
        this.buffer = new CopyOnWriteArrayList<>();
        this.bufferSize = bufferSize;
        this.failSafe = failSafe;
    }

    @Override
    public void startup() {
    }

    @Override
    public void shutdown() {
    }

    /**
     * 写日志
     * 
     * @param info
     *            日志
     */
    protected abstract void writeInternal(LogInfo info);

    protected void writeInternal(List<LogInfo> infos) {
        for (LogInfo info : infos) {
            try {
                this.writeInternal(info);
            } catch (Throwable e) {
                if (!failSafe) {
                    throw e;
                }
                StaticLogger.warn("write log message failed", e);
            }
        }
    }

    @Override
    public void write(LogInfo info) {
        if (this.bufferSize > 0) {
            incrementOrFlush(info);
        } else {
            this.writeInternal(info);
        }
    }

    public void flush() {
        writeInternal(getAndClear());
    }

    /**
     * 向buffer中添加logInfo, 如果size大于等于bufferSize则flush
     * 
     * @param info
     *            日志
     */
    protected void incrementOrFlush(LogInfo info) {
        List<LogInfo> infos = null;
        synchronized (buffer) {
            this.buffer.add(info);
            if (this.buffer.size() >= this.bufferSize) {
                infos = getAndClear();
            }
        }
        if (infos != null) {
            writeInternal(infos);
        }
    }

    /**
     * 获取并清空buffer
     * 
     * @return buffer中的所有logInfos
     */
    protected List<LogInfo> getAndClear() {
        List<LogInfo> result = new ArrayList<>();
        synchronized (buffer) {
            for (LogInfo info : buffer) {
                result.add(info);
            }
            buffer.clear();
        }
        return result;
    }

}
