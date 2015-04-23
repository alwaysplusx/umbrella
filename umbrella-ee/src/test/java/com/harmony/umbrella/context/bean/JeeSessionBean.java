package com.harmony.umbrella.context.bean;

import javax.ejb.Stateless;

@Stateless(name = "JeeBean", mappedName = "JeeSessionBean", description = "java environment bean")
public class JeeSessionBean implements JeeSessionRemote, JeeSessionLocal {

}
