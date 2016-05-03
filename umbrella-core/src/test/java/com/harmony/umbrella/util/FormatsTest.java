package com.harmony.umbrella.util;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class FormatsTest {

    @Test
    public void testNullableNumberFormat() throws ParseException {

        /*BigDecimal value = new BigDecimal("0.231");

        NullableNumberFormat nnf = Formats.createNumberFormat(2, RoundingMode.HALF_UP);

        BigDecimal v1 = nnf.bigDecimalValue(value);
        assertEquals(new BigDecimal("0.23"), v1);

        Double v2 = nnf.doubleValue(value);
        assertEquals(new Double(0.23), v2);

        String v3 = nnf.format(value);
        assertEquals("0.23", v3);

        Long v4 = nnf.longValue(value);
        assertEquals(Long.valueOf(0), v4);

        String source = "0.231";

        Number v5 = nnf.parse(source);
        assertEquals(0.23, v5.doubleValue(), 0);

        BigDecimal v6 = nnf.parseBigDecimal(source);
        assertEquals(new BigDecimal("0.23"), v6);

        Double v7 = nnf.parseDouble(source);
        assertEquals(new Double("0.23"), v7);

        Long v8 = nnf.parseLong(source);
        assertEquals(Long.valueOf(0), v8);*/

    }

    public static void main(String[] args) throws Exception {
        DecimalFormat df = new DecimalFormat("#.#########");
        System.out.println(df.format(1.2));
    }
}
