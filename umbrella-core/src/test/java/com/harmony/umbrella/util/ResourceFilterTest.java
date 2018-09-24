package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceFilterTest {

    @Test
    public void testMode() {
        PatternResourceFilter<String> filter = new PatternResourceFilter();
        filter.addExcludes("**/test/*.*");
        filter.addIncludes("**/*.txt");

        assertFalse(filter.accept("com/harmony/test/a.txt"));
        assertTrue(filter.accept("com/harmony/foo/a.txt"));
    }

}
