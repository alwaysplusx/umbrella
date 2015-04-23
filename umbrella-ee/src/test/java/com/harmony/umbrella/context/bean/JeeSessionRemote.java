package com.harmony.umbrella.context.bean;

import javax.ejb.Remote;

@Remote
public interface JeeSessionRemote {

	String sayHi();

}