package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.harmony.umbrella.access.AccessorHolder;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.MemberAccess;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.GenericUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public abstract class XmlBeanMapper extends ElementAcceptor {

    /**
     * 所有支持string转化的converter
     */
    private static final List<Converter> allStringConverters;

    static {
        final List<Converter> cvs = new ArrayList<Converter>();
        ResourceManager.getInstance().getClasses(ApplicationContext.APPLICATION_PACKAGE, new ClassFilter() {

            @Override
            public boolean accept(Class<?> clazz) {
                if (Converter.class.isAssignableFrom(clazz) //
                        && ClassFilterFeature.NEWABLE.accept(clazz)//
                        && GenericUtils.getTargetGeneric(clazz, Converter.class, 0) == String.class) {
                    cvs.add((Converter) ReflectionUtils.instantiateClass(clazz));
                }
                return false;
            }
        });
        allStringConverters = cvs;
    }

    protected AccessorHolder accessorHolder;

    // 标志位
    private boolean root = true;
    protected String rootPath;

    protected Object result;

    public XmlBeanMapper() {
    }

    protected abstract Class<?> getMappedType();

    @Override
    public boolean acceptElement(String path, Element element) {
        if (root) {
            root = false;
            rootPath = path;
            result = instanceBean();
            return true;
        }
        setMemberValue(result, toFieldPath(path), element);
        return true;
    }

    protected abstract void setMemberValue(Object target, String fieldPath, Element element) throws MappingException;

    protected String getElemenetValue(Element element) {
        String value = element.getNodeValue();
        return StringUtils.isBlank(value) ? element.getAttribute("value") : value;
    }

    protected Class<?> getFieldType(String path) {
        return MemberAccess.access(getMappedType(), path).getType();
    }

    /**
     * 映射的结果
     * 
     * @return
     */
    public Object getResult() {
        return result;
    }

    /**
     * 根据目标类型加载对应的string converter
     * 
     * @param requireType
     *            映射的目标类型
     * @return
     */
    public Converter getConvert(Class<?> requireType) {
        // 将元数据类型转为封装类型
        if (requireType.isPrimitive()) {
            requireType = ClassUtils.getPrimitiveWrapperType(requireType);
        }
        for (Converter converter : allStringConverters) {
            Class<? extends Converter> clazz = converter.getClass();
            if (requireType.isAssignableFrom(GenericUtils.getTargetGeneric(clazz, Converter.class, 1))) {
                return converter;
            }
        }
        return null;
    }

    protected String toFieldPath(String path) {
        return path.substring(rootPath.length() + 1).replace(XmlUtil.PATH_SPLIT, ".");
        // return ROOT + "." + path.substring(rootPath.length() + 1).replace(XmlUtil.PATH_SPLIT, ".");
    }

    /**
     * 初始化映射的对象
     * 
     * @param element
     */
    protected Object instanceBean() {
        return ReflectionUtils.instantiateClass(getMappedType());
    }

}