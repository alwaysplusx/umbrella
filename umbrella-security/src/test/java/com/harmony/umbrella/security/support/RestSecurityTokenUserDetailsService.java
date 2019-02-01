package com.harmony.umbrella.security.support;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.userdetails.SecurityTokenUserDetailsService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author wuxii
 */
public class RestSecurityTokenUserDetailsService implements SecurityTokenUserDetailsService {

	protected String url;

	protected RestTemplate restTemplate;

	protected HttpMethod httpMethod = HttpMethod.GET;

	protected Converter<SecurityToken, Map<String, Object>> requestConverter;

	protected Class<? extends UserDetails> userDetailsType = User.class;

	public RestSecurityTokenUserDetailsService(String url) {
		this.url = url;
		this.restTemplate = new RestTemplate();
	}

	public RestSecurityTokenUserDetailsService(String url, RestTemplate restTemplate, Class<? extends UserDetails> userDetailsType) {
		this.url = url;
		this.restTemplate = restTemplate;
		this.userDetailsType = userDetailsType;
	}

	@Override
	public UserDetails loadUserBySecurityToken(SecurityToken securityToken) {
		Object request = securityToken;
		if (requestConverter != null) {
			request = requestConverter.convert(securityToken);
		}
		ResponseEntity<? extends UserDetails> responseEntity = restTemplate.getForEntity(url, userDetailsType);
		if (!responseEntity.getStatusCode().is2xxSuccessful()) {
			throw new AuthenticationServiceException("can't get security token user details by rest request. " + url);
		}
		return responseEntity.getBody();
	}

	public void setRequestConverter(Converter<SecurityToken, Map<String, Object>> requestConverter) {
		this.requestConverter = requestConverter;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setUserDetailsType(Class<? extends UserDetails> userDetailsType) {
		this.userDetailsType = userDetailsType;
	}

}
