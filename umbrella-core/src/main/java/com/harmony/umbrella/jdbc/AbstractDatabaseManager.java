package com.harmony.umbrella.jdbc;

import java.util.ArrayList;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractDatabaseManager<T> implements DatabaseManager<T> {

    private final ArrayList<WrapEvent> buffer;
    private final int bufferSize;

    public AbstractDatabaseManager(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new ArrayList<WrapEvent>(bufferSize + 1);
    }

    protected abstract void commitAndClose();

    protected abstract void writeInternal(LogInfo logInfo, T event);

    protected abstract void connectAndStart();

    @Override
    public synchronized void write(LogInfo logInfo, T event) {
        if (this.bufferSize > 0) {
            this.buffer.add(new WrapEvent(logInfo, event));
            if (this.buffer.size() >= this.bufferSize) {
                this.flush();
            }
        } else {
            this.connectAndStart();
            try {
                this.writeInternal(logInfo, event);
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
                for (WrapEvent event : this.buffer) {
                    this.writeInternal(event.logInfo, event.event);
                }
            } finally {
                this.commitAndClose();
                // not sure if this should be done when writing the events failed
                this.buffer.clear();
            }
        }
    }

    protected class WrapEvent {
        public final LogInfo logInfo;
        public final T event;

        public WrapEvent(LogInfo logInfo, T event) {
            this.logInfo = logInfo;
            this.event = event;
        }
    }
}
