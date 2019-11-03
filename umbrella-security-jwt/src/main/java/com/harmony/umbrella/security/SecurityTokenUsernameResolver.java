package com.harmony.umbrella.security;

public interface SecurityTokenUsernameResolver {

    String resolve(SecurityToken securityToken);

}
