package com.harmony.umbrella.ee.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class BeanInterfaceResolver implements PartResolver<Class> {

    private LocalInterfaceTransformer localInterfaceTransformer;

    @Override
    public Set<Class> resolve(BeanDefinition bd) {
        Set<Class> result = new HashSet<Class>();
        Class<?>[] remoteClasses = bd.getRemoteClasses();
        if (remoteClasses != null && remoteClasses.length > 0) {
            Collections.addAll(result, remoteClasses);
            return result;
        }

        Class<?>[] localClasses = bd.getLocalClasses();
        if (localClasses != null && localClasses.length > 0 && localInterfaceTransformer != null) {
            for (Class<?> c : localClasses) {
                Class[] classes = localInterfaceTransformer.transform(c);
                for (Class c1 : classes) {
                    if (c1.isInterface()) {
                        result.add(c1);
                    }
                }
            }
        }

        return result;
    }

}
