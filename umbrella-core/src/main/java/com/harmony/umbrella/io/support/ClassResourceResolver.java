package com.harmony.umbrella.io.support;

import java.io.IOException;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceLoader;

/**
 * @author wuxii@foxmail.com
 */
public interface ClassResourceResolver extends ResourceLoader {

    Resource[] getResources(String locationPattern) throws IOException;

    Class<?>[] getClassResources(String locationPattern) throws IOException;

}
