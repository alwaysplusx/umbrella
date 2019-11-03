package com.harmony.umbrella.security.jwt.support;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.SecurityTokenExtractor;

import javax.servlet.http.HttpServletRequest;

public class JwtSecurityTokenExtract implements SecurityTokenExtractor {

    public static final String SCHEMA = "bearer";

    public static final JwtSecurityTokenExtract INSTANCE = new JwtSecurityTokenExtract(SCHEMA);

    private final String schema;

    public JwtSecurityTokenExtract(String schema) {
        this.schema = schema;
    }

    @Override
    public SecurityToken extract(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith(schema + " ")) {
            return null;
        }
        return new SecurityToken(schema, header.substring(schema.length() + 1));
    }

}
