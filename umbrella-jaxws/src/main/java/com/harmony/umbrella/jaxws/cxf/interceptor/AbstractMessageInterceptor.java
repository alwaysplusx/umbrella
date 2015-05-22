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
package com.harmony.umbrella.jaxws.cxf.interceptor;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.common.util.StringUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.staxutils.PrettyPrintXMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageInterceptor extends AbstractPhaseInterceptor<Message> {

    public static final int DEFAULT_LIMIT = 48 * 1024;

    protected int limit = DEFAULT_LIMIT;

    protected boolean prettyLogging = true;

    public AbstractMessageInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        logging(buildLoggingMessage(message));
    }

    protected abstract void logging(LoggingMessage loggingMessage);

    protected LoggingMessage buildLoggingMessage(Message message) throws Fault {
        String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }

        final LoggingMessage buffer = new LoggingMessage("Logging Message\n---------------------------", id);

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }

        String encoding = (String) message.get(Message.ENCODING);
        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }
        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String address = (String) message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
            String uri = (String) message.get(Message.REQUEST_URI);
            if (uri != null && !address.startsWith(uri)) {
                if (!address.endsWith("/") && !uri.startsWith("/")) {
                    buffer.getAddress().append("/");
                }
                buffer.getAddress().append(uri);
            }
        }
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        InputStream is = message.getContent(InputStream.class);
        if (is != null) {
        }
        return buffer;

    }

    protected void writePayload(StringBuilder builder, CachedOutputStream cos, String encoding, String contentType) throws Exception {
        // Just transform the XML message when the cos has content
        if (isPrettyLogging() && (contentType != null && contentType.indexOf("xml") >= 0 && contentType.toLowerCase().indexOf("multipart/related") < 0)
                && cos.size() > 0) {

            StringWriter swriter = new StringWriter();
            XMLStreamWriter xwriter = StaxUtils.createXMLStreamWriter(swriter);
            xwriter = new PrettyPrintXMLStreamWriter(xwriter, 2);
            InputStream in = cos.getInputStream();
            try {
                StaxUtils.copy(new StreamSource(in), xwriter);
            } catch (XMLStreamException xse) {
                // ignore
            } finally {
                try {
                    xwriter.flush();
                    xwriter.close();
                } catch (XMLStreamException xse2) {
                    // ignore
                }
                in.close();
            }

            String result = swriter.toString();
            if (result.length() < limit || limit == -1) {
                builder.append(swriter.toString());
            } else {
                builder.append(swriter.toString().substring(0, limit));
            }

        } else {
            if (StringUtils.isEmpty(encoding)) {
                cos.writeCacheTo(builder, limit);
            } else {
                cos.writeCacheTo(builder, encoding, limit);
            }
        }
    }

    protected void writePayload(StringBuilder builder, StringWriter stringWriter, String contentType) throws Exception {
        // Just transform the XML message when the cos has content
        if (isPrettyLogging() && contentType != null && contentType.indexOf("xml") >= 0 && stringWriter.getBuffer().length() > 0) {

            StringWriter swriter = new StringWriter();
            XMLStreamWriter xwriter = StaxUtils.createXMLStreamWriter(swriter);
            xwriter = new PrettyPrintXMLStreamWriter(xwriter, 2);
            StaxUtils.copy(new StreamSource(new StringReader(stringWriter.getBuffer().toString())), xwriter);
            xwriter.close();

            String result = swriter.toString();
            if (result.length() < limit || limit == -1) {
                builder.append(swriter.toString());
            } else {
                builder.append(swriter.toString().substring(0, limit));
            }

        } else {
            StringBuffer buffer = stringWriter.getBuffer();
            if (buffer.length() > limit) {
                builder.append(buffer.subSequence(0, limit));
            } else {
                builder.append(buffer);
            }
        }
    }

    public boolean isPrettyLogging() {
        return prettyLogging;
    }

    public void setPrettyLogging(boolean prettyLogging) {
        this.prettyLogging = prettyLogging;
    }

}

//
//
//
// // ################################################################
// if (message.containsKey(LoggingMessage.ID_KEY)) {
// return;
// }
// String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
// if (id == null) {
// id = LoggingMessage.nextId();
// message.getExchange().put(LoggingMessage.ID_KEY, id);
// }
// message.put(LoggingMessage.ID_KEY, id);
// final LoggingMessage buffer = new
// LoggingMessage("Inbound Message\n----------------------------", id);
//
// if (!Boolean.TRUE.equals(message.get(Message.DECOUPLED_CHANNEL_MESSAGE))) {
// // avoid logging the default responseCode 200 for the decoupled
// // responses
// Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
// if (responseCode != null) {
// buffer.getResponseCode().append(responseCode);
// }
// }
//
// String encoding = (String) message.get(Message.ENCODING);
//
// if (encoding != null) {
// buffer.getEncoding().append(encoding);
// }
// String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
// if (httpMethod != null) {
// buffer.getHttpMethod().append(httpMethod);
// }
// String ct = (String) message.get(Message.CONTENT_TYPE);
// if (ct != null) {
// buffer.getContentType().append(ct);
// }
// Object headers = message.get(Message.PROTOCOL_HEADERS);
//
// if (headers != null) {
// buffer.getHeader().append(headers);
// }
// String uri = (String) message.get(Message.REQUEST_URL);
// if (uri == null) {
// String address = (String) message.get(Message.ENDPOINT_ADDRESS);
// uri = (String) message.get(Message.REQUEST_URI);
// if (uri != null && uri.startsWith("/")) {
// if (address != null && !address.startsWith(uri)) {
// if (address.endsWith("/") && address.length() > 1) {
// address = address.substring(0, address.length());
// }
// uri = address + uri;
// }
// } else {
// uri = address;
// }
// }
// if (uri != null) {
// buffer.getAddress().append(uri);
// String query = (String) message.get(Message.QUERY_STRING);
// if (query != null) {
// buffer.getAddress().append("?").append(query);
// }
// }
//
// if (!isShowBinaryContent() && isBinaryContent(ct)) {
// buffer.getMessage().append(BINARY_CONTENT_MESSAGE).append('\n');
// log(logger, buffer.toString());
// return;
// }
//
// InputStream is = message.getContent(InputStream.class);
// if (is != null) {
// logInputStream(message, is, buffer, encoding, ct);
// } else {
// Reader reader = message.getContent(Reader.class);
// if (reader != null) {
// logReader(message, reader, buffer);
// }
// }