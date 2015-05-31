/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.jaxws.cxf.log;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.namespace.QName;

import com.harmony.umbrella.util.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class LogMessage {

	public static final String ID_KEY = LogMessage.class.getName() + ".ID";

	private static final AtomicInteger ID = new AtomicInteger();

	private final String id;
	private final String heading;
	private final StringBuilder address;
	private final StringBuilder contentType;
	private final StringBuilder encoding;
	private final StringBuilder httpMethod;
	private final StringBuilder header;
	private final StringBuilder message;
	private final StringBuilder payload;
	private final StringBuilder responseCode;
	private QName service;
	private QName proxy;
	private QName operation;
	private String wsdlUrl;
	private Throwable exception;

	public LogMessage(String heading, String id) {
		this.heading = heading;
		this.id = id;
		this.contentType = new StringBuilder();
		this.address = new StringBuilder();
		this.encoding = new StringBuilder();
		this.httpMethod = new StringBuilder();
		this.header = new StringBuilder();
		this.message = new StringBuilder();
		this.payload = new StringBuilder();
		this.responseCode = new StringBuilder();
	}

	public static String nextId() {
		return Integer.toString(ID.incrementAndGet());
	}

	public String getId() {
		return id;
	}

	public String getHeading() {
		return heading;
	}

	public StringBuilder getAddress() {
		return address;
	}

	public StringBuilder getContentType() {
		return contentType;
	}

	public StringBuilder getEncoding() {
		return encoding;
	}

	public StringBuilder getHttpMethod() {
		return httpMethod;
	}

	public StringBuilder getHeader() {
		return header;
	}

	public StringBuilder getMessage() {
		return message;
	}

	public StringBuilder getPayload() {
		return payload;
	}

	public StringBuilder getResponseCode() {
		return responseCode;
	}

	public QName getProxy() {
		return proxy;
	}

	public QName getService() {
		return service;
	}

	public void setProxy(QName proxy) {
		this.proxy = proxy;
	}

	public void setService(QName service) {
		this.service = service;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public QName getOperation() {
		return operation;
	}

	public void setOperation(QName operation) {
		this.operation = operation;
	}

    public boolean isException() {
        return exception != null;
    }
	
	public Throwable getException() {
        return exception;
    }
	
	public void setException(Throwable exception) {
        this.exception = exception;
    }
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(heading);
		buffer.append("\nID: ").append(id);

		if (address.length() > 0) {
			buffer.append("\nAddress: ");
			buffer.append(address);
		}

		if (responseCode.length() > 0) {
			buffer.append("\nResponse-Code: ");
			buffer.append(responseCode);
		}

		if (encoding.length() > 0) {
			buffer.append("\nEncoding: ");
			buffer.append(encoding);
		}

		if (httpMethod.length() > 0) {
			buffer.append("\nHttp-Method: ");
			buffer.append(httpMethod);
		}

		buffer.append("\nContent-Type: ");
		buffer.append(contentType);

		buffer.append("\nHeaders: ");
		buffer.append(header);

		if (message.length() > 0) {
			buffer.append("\nMessages: ");
			buffer.append(message);
		}

		if (isException()) {
		    buffer.append("\nException: ");
		    buffer.append(Exceptions.getRootCause(exception));
		}
		
		if (payload.length() > 0) {
			buffer.append("\nPayload: \n");
			buffer.append(payload);
		}

		if (!buffer.toString().endsWith("\n")) {
		    buffer.append("\n");
		}
		
		buffer.append("--------------------------------------");
		return buffer.toString();
	}

}
