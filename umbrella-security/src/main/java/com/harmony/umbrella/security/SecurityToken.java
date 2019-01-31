package com.harmony.umbrella.security;

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

	public String getSchema() {
		return schema;
	}

	public String getToken() {
		return token;
	}

}
