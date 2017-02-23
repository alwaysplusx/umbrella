package com.harmony.umbrella.cache;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.harmony.umbrella.util.PropertiesUtils;

/**
 * @author wuxii@foxmail.com
 */
public class J2CacheTest {

    @Test
    public void test() throws IOException, InterruptedException {
        Properties props = PropertiesUtils.loadProperties("j2cache.properties");
        J2Cache j2Cache = J2Cache.getInstance();
        j2Cache.init(props);
        j2Cache.getChannel();
        Thread.sleep(1000 * 100);
    }

}
