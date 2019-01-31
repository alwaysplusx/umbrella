package com.harmony.umbrella.security.support;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.userdetails.SecurityTokenUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

/**
 * @author wuxii
 */
public class RestSecurityTokenUserDetailsService implements SecurityTokenUserDetailsService {

    private RestTemplate restTemplate;

    private String url;

    @Override
    public UserDetails loadUserBySecurityToken(SecurityToken securityToken) {
        // TODO use rest template get user details
        ResponseEntity<UserDetails> responseEntity = restTemplate.postForEntity(url, securityToken, UserDetails.class);
        return responseEntity.getBody();
    }

}
