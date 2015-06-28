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
package com.harmony.umbrella.ws.cxf.interceptor;

import static com.harmony.umbrella.ws.cxf.CXFMessageUtils.*;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.ws.cxf.log.LogMessage;
import com.harmony.umbrella.ws.cxf.log.LogMessageHandler;

/**
 * @author wuxii@foxmail.com
 */
public class MessageOutInterceptor extends AbstractMessageInterceptor {

    private LogMessageHandler handler;

    private static final Logger log = LoggerFactory.getLogger(MessageOutInterceptor.class);

    public MessageOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public MessageOutInterceptor(String phase) {
        super(phase);
        addBefore(StaxOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        final OutputStream os = message.getContent(OutputStream.class);
        final Writer writer = message.getContent(Writer.class);
        if (os != null) {
            CacheAndWriteOutputStream cos = new CacheAndWriteOutputStream(os);

            cos.registerCallback(new CachedOutputStreamCallback() {
                @Override
                public void onFlush(CachedOutputStream os) {
                }

                @Override
                public void onClose(CachedOutputStream cos) {
                    message.setContent(OutputStream.class, cos);
                    logging(buildLoggingMessage(message));
                    message.setContent(OutputStream.class, os);
                }
            });

            message.setContent(OutputStream.class, cos);
        } else {
            message.setContent(Writer.class, new FilterWriter(writer) {
                @Override
                public void close() throws IOException {
                    message.setContent(Writer.class, this.out);
                    logging(buildLoggingMessage(message));
                    message.setContent(Writer.class, writer);
                }
            });
        }
    }

    @Override
    protected void logging(LogMessage logMessage) {
        try {
            if (handler != null) {
                handler.handle(logMessage);
            }
        } finally {
            log.info("{}", logMessage);
        }
    }

    @Override
    protected String getPayload(Message message) {
        final OutputStream os = message.getContent(OutputStream.class);
        final Writer writer = message.getContent(Writer.class);
        if (os != null) {
            return getPayloadFromOutputStream(message, os);
        } else if (writer != null) {
            return getPayloadFromWriter(message, writer);
        }
        return "";
    }

    protected String getPayloadFromWriter(Message message, Writer writer) {
        StringBuilder payload = new StringBuilder();
        try {
            writePayload(payload, (StringWriter) (writer instanceof StringWriter ? writer : new StringWriter()), getContentType(message));
        } catch (Exception e) {
            return "Error load payload > " + e.toString();
        }
        return payload.toString();
    }

    protected String getPayloadFromOutputStream(Message message, OutputStream os) {
        CacheAndWriteOutputStream cos = os instanceof CacheAndWriteOutputStream ? (CacheAndWriteOutputStream) os : new CacheAndWriteOutputStream(os);
        StringBuilder payload = new StringBuilder();
        try {
            writePayload(payload, cos, getEncoding(message), getContentType(message));
        } catch (Exception e) {
            return "Error load payload > " + e.toString();
        }
        return payload.toString();
    }

    public LogMessageHandler getHandler() {
        return handler;
    }

    public void setHandler(LogMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    protected String getMessageHeading() {
        return "\n--------------------------------------\nOutbound Message\n--------------------------------------";
    }

}
