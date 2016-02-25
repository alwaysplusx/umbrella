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

import java.io.InputStream;
import java.io.Reader;
import java.io.SequenceInputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedWriter;
import org.apache.cxf.io.DelegatingInputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import com.harmony.umbrella.ws.cxf.log.LogMessageHandler;

/**
 * @author wuxii@foxmail.com
 */
public class MessageInInterceptor extends AbstractMessageInterceptor {

    protected LogMessageHandler handler;

    public MessageInInterceptor() {
        this("Inbound");
    }

    public MessageInInterceptor(String type) {
        this(type, Phase.RECEIVE, null);
    }

    public MessageInInterceptor(String type, String phase, LogMessageHandler handler) {
        super(type, phase);
        this.handler = handler;
    }

    @Override
    protected String getPayload(Message message) {
        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
            return getPayloadFromInputStream(message, is);
        } else {
            Reader reader = message.getContent(Reader.class);
            if (reader != null) {
                return getPayloadFromReader(message, reader);
            }
        }
        return "";
    }

    protected String getPayloadFromReader(Message message, Reader reader) {
        StringBuilder buffer = new StringBuilder();
        try {
            CachedWriter writer = new CachedWriter();
            IOUtils.copyAndCloseInput(reader, writer);
            message.setContent(Reader.class, writer.getReader());
            writer.writeCacheTo(buffer);
        } catch (Exception e) {
            return "Error load payload > " + e.toString();
        }
        return buffer.toString();
    }

    protected String getPayloadFromInputStream(Message message, InputStream is) {
        CachedOutputStream bos = new CachedOutputStream();
        StringBuilder buffer = new StringBuilder();

        try {
            InputStream bis = is instanceof DelegatingInputStream ? ((DelegatingInputStream) is).getInputStream() : is;

            IOUtils.copy(bis, bos);
            bos.flush();

            bis = new SequenceInputStream(bos.getInputStream(), bis);

            // restore the delegating input stream or the input stream
            if (is instanceof DelegatingInputStream) {
                ((DelegatingInputStream) is).setInputStream(bis);
            } else {
                message.setContent(InputStream.class, bis);
            }

            writePayload(buffer, bos, getEncoding(message), getContentType(message));

            bos.close();
        } catch (Exception e) {
            return "Error load payload > " + e.toString();
        }
        return buffer.toString();
    }

    public LogMessageHandler getHandler() {
        return handler;
    }

    public void setHandler(LogMessageHandler handler) {
        this.handler = handler;
    }

}
