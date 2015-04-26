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

/**
 * {@linkplain Context}内容的访问者
 * 
 * @author wuxii@foxmail.com
 */
public class ContextVisitor {

	private boolean isVisitEnd = false;

	public void visitContext(Context context, String jndi) {
	}

	public void visitBean(Object bean, String jndi) {
	}

	public void visitEnd() {
		isVisitEnd = true;
	}

	public boolean isVisitEnd() {
		return isVisitEnd;
	}

}