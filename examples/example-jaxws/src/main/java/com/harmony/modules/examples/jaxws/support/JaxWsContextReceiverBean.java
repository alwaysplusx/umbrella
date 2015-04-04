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
package com.harmony.modules.examples.jaxws.support;

import javax.ejb.Stateless;

import com.harmony.modules.examples.jaxws.JaxWsMessage;
import com.harmony.modules.jaxws.JaxWsContext;
import com.harmony.modules.jaxws.support.JaxWsContextReceiver;
import com.harmony.modules.jaxws.support.JaxWsContextReceiverImpl;
import com.harmony.modules.message.AbstractMessageResolver;
import com.harmony.modules.message.Message;

/**
 * @author wuxii
 */
@Stateless
public class JaxWsContextReceiverBean extends AbstractMessageResolver<JaxWsContext> implements JaxWsContextReceiver {

	private JaxWsContextReceiver receiver = new JaxWsContextReceiverImpl();

	@Override
	public boolean support(Message message) {
		return message instanceof JaxWsMessage;
	}

	@Override
	public void process(JaxWsContext message) {
		receive(message);
	}

	@Override
	protected JaxWsContext resolver(Message message) {
		return ((JaxWsMessage) message).getMessage();
	}

	@Override
	public void receive(JaxWsContext context) {
		receiver.receive(context);
	}

	@Override
	public void open() throws Exception {
		receiver.open();
	}

	@Override
	public void close() throws Exception {
		receiver.close();
	}

	@Override
	public boolean isClosed() {
		return receiver.isClosed();
	}

	public void setJaxWsContextReceiver(JaxWsContextReceiver receiver) {
		this.receiver = receiver;
	}
}
