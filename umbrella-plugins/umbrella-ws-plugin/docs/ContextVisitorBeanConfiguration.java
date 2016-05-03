package com.harmony.umbrella.ws;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.context.ee.EEConfiguration;
import com.harmony.umbrella.ws.visitor.SyncableContextVisitor;

/**
 * @author wuxii@foxmail.com
 */
@Remote(EEConfiguration.class)
@Stateless(mappedName = "ContextVisitorBeanConfiguration")
public class ContextVisitorBeanConfiguration implements EEConfiguration<ContextVisitor> {

    @Override
    public List<ContextVisitor> getConfigBeans() {
        return Arrays.<ContextVisitor> asList(new SyncableContextVisitor());
    }

}
