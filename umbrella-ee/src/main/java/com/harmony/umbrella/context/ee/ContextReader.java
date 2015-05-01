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
package com.harmony.umbrella.context.ee;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@linkplain Context}循环遍历所有内容
 * 
 * @author wuxii@foxmail.com
 */
public class ContextReader {

	private static final Logger log = LoggerFactory.getLogger(ContextReader.class);
	protected final Context context;
	private int count;

	public ContextReader(Context context) {
		this.context = context;
	}

	/**
	 * 接收一个访问者开始迭代
	 * 
	 * @param visitor
	 * @param contextRoot
	 */
	public void accept(ContextVisitor visitor, String contextRoot) {
		try {
			Object obj = context.lookup(contextRoot);
			if (obj instanceof Context) {
				accept0((Context) obj, visitor, contextRoot);
			} else {
				visitor.visitBean(obj, contextRoot);
			}
		} catch (NamingException e) {
		}
	}

	protected void accept0(Context context, ContextVisitor visitor, String contextRoot) {
		count++;
		log.debug("current stack is " + count);
		try {
			NamingEnumeration<Binding> bindings = context.listBindings("");
			while (!visitor.isVisitEnd() && bindings.hasMoreElements()) {
				Binding binding = bindings.next();
				Object object = binding.getObject();
				String jndi = contextRoot + ("".equals(contextRoot) ? "" : "/") + binding.getName();
				if (object instanceof Context) {
					visitor.visitContext((Context) object, jndi);
					accept0((Context) object, visitor, contextRoot);
				} else {
					visitor.visitBean(object, jndi);
				}
			}
		} catch (NamingException e) {
		} finally {
			count--;
		}
	}
}