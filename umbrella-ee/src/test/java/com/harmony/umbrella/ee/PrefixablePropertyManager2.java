package com.harmony.umbrella.ee;

import java.io.IOException;

import com.harmony.umbrella.core.PrefixablePropertyManager;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PrefixablePropertyManager2 extends PrefixablePropertyManager {

    public PrefixablePropertyManager2() {
    }

    public PrefixablePropertyManager2(String fileLocation) throws IOException {
        super(PropertiesUtils.loadProperties(fileLocation));
    }

}
