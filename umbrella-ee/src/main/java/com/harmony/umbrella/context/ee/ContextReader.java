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

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * {@linkplain Context}循环遍历所有内容
 * 
 * @author wuxii@foxmail.com
 */
public class ContextReader {

	private final Context context;

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
		if (visitor.isVisitEnd()) {
			return;
		}
		try {
			Object obj = context.lookup(contextRoot);
			if (obj instanceof Context) {
				Context ctx = (Context) obj;
				visitor.visitContext(ctx, contextRoot);
				NamingEnumeration<NameClassPair> ncps = ctx.list("");
				while (ncps.hasMoreElements()) {
					String jndi = contextRoot + ("".equals(contextRoot) ? "" : "/") + ncps.next().getName();
					accept(visitor, jndi);
				}
			} else {
				visitor.visitBean(obj, contextRoot);
			}
		} catch (NamingException e) {
		}
	}

}