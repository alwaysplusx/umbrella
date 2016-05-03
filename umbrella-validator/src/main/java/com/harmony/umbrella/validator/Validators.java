package com.harmony.umbrella.validator;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.harmony.umbrella.util.StringUtils;

/**
 * 用于做一些简单的检验
 * 
 * @author wuxii@foxmail.com
 */
public class Validators {

    // thread-safe
    private static Validator validator;

    /**
     * Returns an initialized {@link Validator} instance using the factory
     * defaults for message interpolator, traversable resolver and constraint
     * validator factory.
     * <p/>
     * Validator instances can be pooled and shared by the implementation.
     *
     * @return an initialized {@code Validator} instance
     * @see Validation#buildDefaultValidatorFactory()
     * @see ValidatorFactory#getValidator()
     */
    public static Validator getValidator() {
        if (validator == null) {
            validator = getValidatorFactory().getValidator();
        }
        return validator;
    }

    private static ValidatorFactory getValidatorFactory() {
        return Validation.buildDefaultValidatorFactory();
    }

    /**
     * 检查bean的违规信息
     * 
     * @param object
     *            待验证的bean
     * @param groups
     *            验证组
     * @return 所有的违规信息用逗号({@code ,})隔开,如果没有违规信息返回{@code null}
     */
    public static String getViolationMessage(Object object, Class<?>... groups) {
        return getViolationMessage(object, null, groups);
    }

    /**
     * 检查bean的违规信息
     * 
     * @param object
     *            待验证的bean
     * @param visitor
     *            验证的访问， 在基础验证后调用
     * @param groups
     *            验证组
     * @return 所有的违规信息用逗号({@code ,})隔开,如果没有违规信息返回{@code null}
     */
    public static String getViolationMessage(Object object, ValidVisitor visitor, Class<?>... groups) {
        StringBuilder buf = new StringBuilder();

        Iterator<ConstraintViolation<Object>> it = getValidator().validate(object, groups).iterator();
        while (it.hasNext()) {
            buf.append(it.next().getMessage());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        if (visitor != null) {
            String message = visitor.valid(object);
            if (!StringUtils.isBlank(message)) {
                buf.append(", ").append(message);
            }
        }

        return buf.length() == 0 ? null : buf.toString();
    }

    /**
     * 检查bean的违规信息条数
     * 
     * @param object
     *            待验证的bean
     * @param groups
     *            验证组
     * @return 违规数据数量
     */
    public static int getNumberOfViolations(Object object, Class<?>... groups) {
        return getValidator().validate(object, groups).size();
    }

    /**
     * 检查bean是否违规
     * 
     * @param object
     *            待验证的bean
     * @param groups
     *            验证组
     * 
     * @return true检测对象违规
     */
    public static boolean isViolation(Object object, Class<?>... groups) {
        return getNumberOfViolations(object, groups) > 0;
    }

}
