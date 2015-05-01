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
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.ContextReader;
import com.harmony.umbrella.context.ee.ContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class WeblogicContextReader extends ContextReader {

	private static final Object lockObject = new Object();

	private static final Logger log = LoggerFactory.getLogger(WeblogicContextReader.class);

	private long waitTime;
	private boolean interrupted;

	public WeblogicContextReader(Context context, long waitTime) {
		super(context);
		this.waitTime = waitTime;
	}

	@Override
	public void accept(final ContextVisitor visitor, final String contextRoot) {
		interrupted = false;
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (lockObject) {
						try {
							Object obj = context.lookup(contextRoot);
							if (obj instanceof Context) {
								accept0(context, visitor, contextRoot);
							} else {
								visitor.visitBean(obj, contextRoot);
							}
						} catch (NamingException e) {
							log.debug("", e);
						}
					}
				}
			}).start();

			synchronized (lockObject) {
				log.debug("wait {} for iterator context", waitTime);
				long start = System.currentTimeMillis();
				while (System.currentTimeMillis() - start < waitTime) {
					lockObject.wait();
				}
				log.info("already wait {}", System.currentTimeMillis() - start);
			}
		} catch (InterruptedException e) {
			log.error("", e);
		} finally {
			interrupted = true;
		}
	}

	@Override
	protected void accept0(Context context, ContextVisitor visitor, String contextRoot) {
		lockObject.notify();
		if (interrupted) {
			log.info("reader context timeout, time limited is {}", waitTime);
			return;
		}
		try {
			Object obj = context.lookup(contextRoot);
			if (obj instanceof Context) {
				Context ctx = (Context) obj;
				visitor.visitContext(ctx, contextRoot);
				NamingEnumeration<NameClassPair> ncps = ctx.list("");
				while (ncps.hasMoreElements() && !visitor.isVisitEnd()) {
					String jndi = contextRoot + ("".equals(contextRoot) ? "" : "/") + ncps.next().getName();
					accept0(context, visitor, jndi);
				}
			} else {
				visitor.visitBean(obj, contextRoot);
			}
		} catch (NamingException e) {
		}
	}

}
