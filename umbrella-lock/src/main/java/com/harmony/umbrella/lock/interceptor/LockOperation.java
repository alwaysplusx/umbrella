package com.harmony.umbrella.lock.interceptor;

import lombok.Builder;
import lombok.Getter;

/**
 * @author wuxii
 */
@Builder
@Getter
public class LockOperation {

	private final String name;

	private final String key;

	private final String keyGenerator;

	private final int timeout;

	protected LockOperation(String name, String key, String keyGenerator, int timeout) {
		this.name = name;
		this.key = key;
		this.keyGenerator = keyGenerator;
		this.timeout = timeout;
	}

}
