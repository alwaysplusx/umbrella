package com.harmony.umbrella.security;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.SecurityTokenExtractor;
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
		return extractors.stream().map(e -> e.extract(request)).findAny().orElse(null);
	}

	private void setSecurityTokenExtractors(List<SecurityTokenExtractor> extractors) {
		this.extractors = new ArrayList<>(extractors);
		OrderComparator.sort(this.extractors);
	}

}
