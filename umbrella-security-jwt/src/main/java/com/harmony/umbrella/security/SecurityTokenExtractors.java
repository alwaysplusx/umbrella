package com.harmony.umbrella.security;

import org.springframework.core.OrderComparator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wuxii
 */
public class SecurityTokenExtractors implements SecurityTokenExtractor {

	private List<SecurityTokenExtractor> extractors;

	public SecurityTokenExtractors(SecurityTokenExtractor... extractors) {
		this.setSecurityTokenExtractors(Arrays.asList(extractors));
	}

	public SecurityTokenExtractors(List<SecurityTokenExtractor> extractors) {
		this.setSecurityTokenExtractors(extractors);
	}

	@Override
	public SecurityToken extract(HttpServletRequest request) {
		SecurityToken securityToken = null;
		for (SecurityTokenExtractor extractor : extractors) {
			securityToken = extractor.extract(request);
			if (securityToken != null) {
				break;
			}
		}
		return securityToken;
	}

	private void setSecurityTokenExtractors(List<SecurityTokenExtractor> extractors) {
		this.extractors = new ArrayList<>(extractors);
		OrderComparator.sort(this.extractors);
	}

}
