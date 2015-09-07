package com.harmony.umbrella.context.bean;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Remote(JeeSessionRemote.class)
@Stateless(mappedName = "JeeSessionBean")
public class JeeSessionBean implements JeeSessionRemote, JeeSessionLocal {

    @Override
    public String sayHi() {
        return null;
    }

}
