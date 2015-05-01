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
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.ContextReader;
import com.harmony.umbrella.context.ee.ContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class TimeLimitedContextReader extends ContextReader {

	private static final Object lockObject = new Object();

	private static final Logger log = LoggerFactory.getLogger(TimeLimitedContextReader.class);

	private long waitTime;
	private boolean interrupted;

	public TimeLimitedContextReader(Context context, long waitTime) {
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
								accept0((Context) obj, visitor, contextRoot);
							} else {
								visitor.visitBean(obj, contextRoot);
							}
						} catch (NamingException e) {
							log.debug("", e);
						}
						lockObject.notify();
					}
				}
			}).start();

			synchronized (lockObject) {
				log.debug("wait {} for iterator context", waitTime);
				lockObject.wait(waitTime);
			}
		} catch (InterruptedException e) {
			log.error("", e);
		} finally {
			interrupted = true;
		}
	}

	@Override
	protected void accept0(Context context, ContextVisitor visitor, String contextRoot) {
		if (interrupted) {
			log.info("reader context timeout, time limited is {}", waitTime);
			return;
		}
		super.accept0(context, visitor, contextRoot);
	}

}
