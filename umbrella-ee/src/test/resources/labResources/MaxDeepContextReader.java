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
package com.harmony.umbrella.context.ee.reader;

import javax.naming.Context;

import com.harmony.umbrella.context.ee.ContextReader;
import com.harmony.umbrella.context.ee.ContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class MaxDeepContextReader extends ContextReader {

	private final int maxDeep;
	private int deepIndex;

	public MaxDeepContextReader(Context context) {
		this(context, 10);
	}

	public MaxDeepContextReader(Context context, int maxDeep) {
		super(context);
		this.maxDeep = maxDeep;
	}

	@Override
	protected void accept0(Context context, ContextVisitor visitor, String contextRoot) {
		try {
			if (++deepIndex > maxDeep) {
				return;
			}
			super.accept0(context, visitor, contextRoot);
		} finally {
			deepIndex--;
		}
	}

}
