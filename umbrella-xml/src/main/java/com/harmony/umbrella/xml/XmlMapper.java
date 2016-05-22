package com.harmony.umbrella.xml;

import org.w3c.dom.Element;

import com.harmony.umbrella.xml.convert.BigDecimalConverter;
import com.harmony.umbrella.xml.convert.BooleanConverter;
import com.harmony.umbrella.xml.convert.CalendarConverter;
import com.harmony.umbrella.xml.convert.DateConverter;
import com.harmony.umbrella.xml.convert.DoubleConverter;
import com.harmony.umbrella.xml.convert.EnumConverter;
import com.harmony.umbrella.xml.convert.FloatConverter;
import com.harmony.umbrella.xml.convert.IntegerConverter;
import com.harmony.umbrella.xml.convert.LongConverter;

/**
 * @author wuxii@foxmail.com
 */
public class XmlMapper {

    private static XmlBeanMapper mapper = new XmlBeanMapper();

    static {
        mapper.addValueConverter(BigDecimalConverter.class);
        mapper.addValueConverter(BooleanConverter.class);
        mapper.addValueConverter(CalendarConverter.class);
        mapper.addValueConverter(DateConverter.class);
        mapper.addValueConverter(DoubleConverter.class);
        mapper.addValueConverter(EnumConverter.class);
        mapper.addValueConverter(FloatConverter.class);
        mapper.addValueConverter(IntegerConverter.class);
        mapper.addValueConverter(LongConverter.class);
    }

    public static <T> T mapping(Element element, Class<T> targetClass) {
        return mapper.mapping(element, targetClass);
    }

    @SuppressWarnings("unchecked")
    public static XmlBeanMapper getXmlBeanMapper() {
        return new XmlBeanMapper(mapper.getValueConverterClasses());
    }

}
