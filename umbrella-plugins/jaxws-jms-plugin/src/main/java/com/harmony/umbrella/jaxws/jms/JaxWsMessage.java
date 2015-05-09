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
package com.harmony.umbrella.jaxws.jms;

import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.message.Message;

/**
 * JaxWs消息模块，可被传输的消息类
 * 
 * @author wuxii
 */
public class JaxWsMessage implements Message {

	private static final long serialVersionUID = -7233998395118539815L;
	private JaxWsContext context;

	public JaxWsMessage() {
	}

	public JaxWsMessage(JaxWsContext context) {
		this.context = context;
	}

	public JaxWsContext getMessage() {
		return context;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JaxWsMessage other = (JaxWsMessage) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		return true;
	}

}
