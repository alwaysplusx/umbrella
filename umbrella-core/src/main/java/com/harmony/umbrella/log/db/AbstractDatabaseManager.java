package com.harmony.umbrella.log.db;

import java.util.ArrayList;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractDatabaseManager implements DatabaseManager {

    private final ArrayList<LogInfo> buffer;
    private final int bufferSize;

    public AbstractDatabaseManager(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new ArrayList<LogInfo>(bufferSize + 1);
    }

    protected abstract void commitAndClose();

    protected abstract void writeInternal(LogInfo logInfo);

    protected abstract void connectAndStart();

    @Override
    public synchronized void write(LogInfo logInfo) {
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

    @Override
    public void flush() {
        if (this.buffer.size() > 0) {
            this.connectAndStart();
            try {
                for (LogInfo logInfo : this.buffer) {
                    this.writeInternal(logInfo);
                }
            } finally {
                this.commitAndClose();
                // not sure if this should be done when writing the events
                // failed
                this.buffer.clear();
            }
        }
    }

}
