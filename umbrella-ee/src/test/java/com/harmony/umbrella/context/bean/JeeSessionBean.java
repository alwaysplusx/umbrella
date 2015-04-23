package com.harmony.umbrella.context.bean;

import javax.ejb.Stateless;

@Stateless(mappedName = "JeeSessionBean")
public class JeeSessionBean implements JeeSessionRemote, JeeSessionLocal {

	@Override
	public String sayHi() {
		return null;
	}

}
