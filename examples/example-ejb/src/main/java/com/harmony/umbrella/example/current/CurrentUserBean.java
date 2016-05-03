package com.harmony.umbrella.example.current;

import javax.ejb.Stateless;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "CurrentUserBean")
public class CurrentUserBean implements CurrentUserRemote {

    @Override
    public String getUser() {
        CurrentContext current = ApplicationContext.getApplicationContext().getCurrentContext();
        return current.getUserCode();
    }

}
