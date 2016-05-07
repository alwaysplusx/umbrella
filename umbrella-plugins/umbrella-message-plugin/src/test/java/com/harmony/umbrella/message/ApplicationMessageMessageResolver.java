package com.harmony.umbrella.message;

import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * @author wuxii@foxmail.com
 */
@Remote(MessageResolver.class)
@Stateless(mappedName = "ApplicationMessageMessageResolver")
public class ApplicationMessageMessageResolver extends TypedMessageResolver<ApplicationMessage> implements MessageResolver {

    @Override
    public void process(ApplicationMessage message) {
        System.out.println(message.getMessage());
    }

}
