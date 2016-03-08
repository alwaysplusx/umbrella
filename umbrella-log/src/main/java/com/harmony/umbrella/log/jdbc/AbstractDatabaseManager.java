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
package com.harmony.umbrella.log.jdbc;

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
