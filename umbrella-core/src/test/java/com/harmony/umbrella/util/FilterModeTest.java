package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class FilterModeTest {

    @Test
    public void testMode() {
        PathFilterMode filter = new PathFilterMode();
        filter.addExcludes("**/test/*.*");
        filter.addIncludes("**/*.txt");

        assertFalse(filter.accept("com/harmony/test/a.txt"));
        assertTrue(filter.accept("com/harmony/foo/a.txt"));
    }

}
