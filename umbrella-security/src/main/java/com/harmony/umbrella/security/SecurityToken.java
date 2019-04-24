package com.harmony.umbrella.security;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author wuxii
 */
public class SecurityToken {

	private final String schema;
	private final String token;

	public SecurityToken(String schema, String token) {
		this.schema = schema;
		this.token = token;
	}

	@Qualifier
	public String getSchema() {
		return schema;
	}

	public String getToken() {
		return token;
	}

	public String getQualifierToken() {
		return schema + " " + token;
	}

}
