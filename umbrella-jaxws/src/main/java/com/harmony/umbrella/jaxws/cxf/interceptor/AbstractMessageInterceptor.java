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

	protected boolean prettyLogging = true;

	public AbstractMessageInterceptor(String phase) {
		super(phase);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		logging(buildLoggingMessage(message));
	}

	protected abstract void logging(LoggingMessage loggingMessage);

	protected abstract String getPayload(Message message);

	protected LoggingMessage buildLoggingMessage(Message message) throws Fault {

		String id = (String) message.getExchange().get(LoggingMessage.ID_KEY);
		if (id == null) {
			id = LoggingMessage.nextId();
			message.getExchange().put(LoggingMessage.ID_KEY, id);
		}

		final LoggingMessage logMessage = new LoggingMessage(getMessageHeading(), id);

		Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
		if (responseCode != null) {
			logMessage.getResponseCode().append(responseCode);
		}

		String encoding = (String) message.get(Message.ENCODING);
		if (encoding != null) {
			logMessage.getEncoding().append(encoding);
		}

		String httpMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
		if (httpMethod != null) {
			logMessage.getHttpMethod().append(httpMethod);
		}

		String address = (String) message.get(Message.ENDPOINT_ADDRESS);
		if (address != null) {
			logMessage.getAddress().append(address);
			String uri = (String) message.get(Message.REQUEST_URI);
			if (uri != null && !address.startsWith(uri)) {
				if (!address.endsWith("/") && !uri.startsWith("/")) {
					logMessage.getAddress().append("/");
				}
				logMessage.getAddress().append(uri);
			}
		}

		String ct = (String) message.get(Message.CONTENT_TYPE);
		if (ct != null) {
			logMessage.getContentType().append(ct);
		}

		Object headers = message.get(Message.PROTOCOL_HEADERS);
		if (headers != null) {
			logMessage.getHeader().append(headers);
		}

		String payload = getPayload(message);
		if (payload != null) {
			logMessage.getPayload().append(payload);
		}

		return logMessage;

	}

	protected void writePayload(StringBuilder builder, CachedOutputStream cos, String encoding, String contentType) throws Exception {

		// Just transform the XML message when the cos has content
		if (isPrettyLogging() && (contentType != null && contentType.indexOf("xml") >= 0 && contentType.toLowerCase().indexOf("multipart/related") < 0) && cos.size() > 0) {

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

	protected String getMessageHeading() {
		return "\n--------------------------------------\nLogging Message\n--------------------------------------";
	}

	public boolean isPrettyLogging() {
		return prettyLogging;
	}

	public void setPrettyLogging(boolean prettyLogging) {
		this.prettyLogging = prettyLogging;
	}

	protected String formatLogMessage(LoggingMessage lm) {
		if (lm == null)
			return null;
		StringBuilder buffer = new StringBuilder();
		buffer.append(getMessageHeading());
		buffer.append("\nID: ").append(lm.getId());
		if (lm.getAddress().length() > 0) {
			buffer.append("\nAddress: ");
			buffer.append(lm.getAddress());
		}
		if (lm.getResponseCode().length() > 0) {
			buffer.append("\nResponse-Code: ");
			buffer.append(lm.getResponseCode());
		}
		if (lm.getEncoding().length() > 0) {
			buffer.append("\nEncoding: ");
			buffer.append(lm.getEncoding());
		}
		if (lm.getHttpMethod().length() > 0) {
			buffer.append("\nHttp-Method: ");
			buffer.append(lm.getHttpMethod());
		}
		buffer.append("\nContent-Type: ");
		buffer.append(lm.getContentType());
		buffer.append("\nHeaders: ");
		buffer.append(lm.getHeader());
		if (lm.getMessage().length() > 0) {
			buffer.append("\nMessages: ");
			buffer.append(lm.getMessage());
		}
		if (lm.getPayload().length() > 0) {
			buffer.append("\nPayload: \n");
			buffer.append(lm.getPayload());
			if (!buffer.toString().endsWith("\n")) {
				buffer.append("\n");
			}
		}
		buffer.append("--------------------------------------");
		return buffer.toString();
	}
}