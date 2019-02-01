package com.harmony.umbrella.security.support;

import com.harmony.umbrella.security.SecurityToken;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wuxii
 */
public class RestSecurityTokenUserDetailsServiceTest {

	@Test
	public void test() {
		RestSecurityTokenUserDetailsService service = new RestSecurityTokenUserDetailsService("https://api.myjson.com/bins/115no8");
		UserDetails userDetails = service.loadUserBySecurityToken(new SecurityToken("base64", "username:password"));
		Assert.assertNotNull(userDetails);
	}

}
