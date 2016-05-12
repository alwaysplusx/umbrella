package com.harmony.umbrella.xml;

import org.w3c.dom.Element;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class XmlMapper {

    /**
     * 通过xml节点中指定的mapper将节点映射为对应的java对象
     * 
     * @param element
     *            映射的xml节点
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T mapping(Element element) {
        String mapperName = element.getAttribute("mapper");
        if (StringUtils.isBlank(mapperName)) {
            throw new MappingException("unknow element mapper, please set attribute 'mapper'");
        }
        try {
            Class<?> mapperClass = ClassUtils.forName(mapperName);
            if (XmlBeanMapper.class.isAssignableFrom(mapperClass)) {
                XmlBeanMapper mapper = (XmlBeanMapper) ReflectionUtils.instantiateClass(mapperClass);
                return (T) mapping(element, mapper);
            }
            throw new MappingException(mapperName + " is not XmlBeanMapper");
        } catch (Exception e) {
            throw new MappingException("unsupported mapper type of " + mapperName, e);
        }
    }

    /**
     * 将xml的节点映射为单个java对象
     * 
     * @param element
     *            xml节点
     * @param mappedType
     *            映射的java类型
     * @return
     */
    public static <T> T mapping(Element element, Class<T> mappedType) {
        return null;
    }

    /**
     * 使用指定的XmlBeanMapper将节点映射为java对象
     * 
     * @param element
     *            xml节点
     * @param mapperType
     *            映射mapper
     * @return
     */
    public static <T> T mappingByMapper(Element element, Class<? extends XmlBeanMapper> mapperType) {
        XmlBeanMapper mapper = ReflectionUtils.instantiateClass(mapperType);
        return mapping(element, mapper);
    }

    @SuppressWarnings("unchecked")
    static <T> T mapping(Element element, XmlBeanMapper mapper) {
        iteratorMapping(XmlUtil.iterator(element), mapper);
        return (T) mapper.getResult();
    }

    private static void iteratorMapping(ElementIterator eit, XmlBeanMapper mapper) {
        if (!mapper.accept(eit.getPath(), eit.getCurrentElement())) {
            return;
        }
        while (eit.hasNext()) {
            iteratorMapping(eit.next(), mapper);
        }
    }

}
