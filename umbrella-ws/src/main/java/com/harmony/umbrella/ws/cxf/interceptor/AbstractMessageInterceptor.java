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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.staxutils.PrettyPrintXMLStreamWriter;
import org.apache.cxf.staxutils.StaxUtils;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.cxf.log.CXFLogMessage;
import com.harmony.umbrella.ws.cxf.log.CXFLogMessageHandler;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Log log = Logs.getLog(AbstractMessageInterceptor.class);

    protected boolean prettyLogging = true;

    private final String type;

    protected CXFLogMessageHandler handler;

    public AbstractMessageInterceptor(String type, String phase) {
        super(phase);
        this.type = type;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        logging(buildLoggingMessage(message));
    }

    @Override
    public void handleFault(Message message) {
        logging(buildLoggingMessage(message));
    }

    protected void logging(CXFLogMessage loggingMessage) {
        if (handler != null) {
            try {
                handler.handle(loggingMessage);
            } catch (Exception e) {
                log.debug("handle log message throw exception", e);
            }
        } else {
            log.info("{}", loggingMessage);
        }
    }

    protected abstract String getPayload(Message message);

    protected CXFLogMessage buildLoggingMessage(Message message) throws Fault {

        Exchange exchange = message.getExchange();
        Message otherMessage = exchange.getInMessage() == message ? exchange.getOutMessage() : exchange.getInMessage();

        String id = (String) message.getExchange().get(CXFLogMessage.ID_KEY);
        if (id == null) {
            id = CXFLogMessage.nextId();
            message.getExchange().put(CXFLogMessage.ID_KEY, id);
        }

        final CXFLogMessage cXFLogMessage = new CXFLogMessage(id, type);

        boolean proxyFlag = (Boolean) (message.get(Message.REQUESTOR_ROLE) == null ? false : message.get(Message.REQUESTOR_ROLE));

        String address, operationName = null;

        if (proxyFlag) {
            // 判断是否是客户端来的消息
            cXFLogMessage.setType("Proxy-" + type);

            address = (String) message.get(Message.ENDPOINT_ADDRESS);
            if (address == null && otherMessage != null) {
                address = (String) otherMessage.get(Message.ENDPOINT_ADDRESS);
            }

            Method method = message.get(Method.class);
            if (method == null && otherMessage != null) {
                method = otherMessage.get(Method.class);
            }

            if (method != null) {
                operationName = StringUtils.getMethodId(method);
            }

        } else {
            // 来自服务端的调用
            cXFLogMessage.setType("Server-" + type);

            address = (String) message.get(Message.REQUEST_URL);
            if (address == null && otherMessage != null) {
                address = (String) otherMessage.get(Message.REQUEST_URL);
            }

            /*
            // 服务实例的服务方法与wsdl的对应关系分发器
            MethodDispatcher md = (MethodDispatcher) message.getContextualProperty(org.apache.cxf.service.invoker.MethodDispatcher.class.getName());*/

            QName qname = (QName) message.get(Message.WSDL_OPERATION);
            if (qname == null && otherMessage != null) {
                qname = (QName) otherMessage.get(Message.WSDL_OPERATION);
            }

            if (qname != null) {
                operationName = qname.toString();
            }

        }

        if (address != null) {
            cXFLogMessage.getAddress().append(address);
        }

        if (operationName != null) {
            cXFLogMessage.setOperationName(operationName);
        }

        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            cXFLogMessage.getResponseCode().append(responseCode);
        }

        String encoding = (String) message.get(Message.ENCODING);
        if (encoding != null) {
            cXFLogMessage.getEncoding().append(encoding);
        }

        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            cXFLogMessage.getHeader().append(headers);
        }

        String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            cXFLogMessage.getHttpMethod().append(httpMethod);
        }

        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            cXFLogMessage.getContentType().append(ct);
        }

        String payload = getPayload(message);
        if (payload != null) {
            cXFLogMessage.getPayload().append(payload);
        }

        Exception ex = message.getContent(Exception.class);
        if (ex != null) {
            cXFLogMessage.setException(ex);
        }

        return cXFLogMessage;

    }

    public String getType() {
        return type;
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
            builder.append(swriter.toString());

        } else {
            if (StringUtils.isEmpty(encoding)) {
                cos.writeCacheTo(builder);
            } else {
                cos.writeCacheTo(builder, encoding);
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
            builder.append(swriter.toString());

        } else {
            builder.append(stringWriter.getBuffer());
        }
    }

    public boolean isPrettyLogging() {
        return prettyLogging;
    }

    public void setPrettyLogging(boolean prettyLogging) {
        this.prettyLogging = prettyLogging;
    }

    public CXFLogMessageHandler getLogMessageHandler() {
        return handler;
    }

    public void setLogMessageHandler(CXFLogMessageHandler handler) {
        this.handler = handler;
    }
}