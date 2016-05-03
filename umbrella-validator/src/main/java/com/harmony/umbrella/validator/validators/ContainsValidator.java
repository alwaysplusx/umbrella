package com.harmony.umbrella.validator.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.harmony.umbrella.validator.constraints.Contains;

/**
 * @author wuxii@foxmail.com
 */
public class ContainsValidator implements ConstraintValidator<Contains, String> {

    private String[] contents;
    private boolean all;

    @Override
    public void initialize(Contains annotation) {
        this.contents = annotation.contents();
        this.all = annotation.all();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        for (String content : contents) {
            boolean contains = value.contains(content);
            if (contains && !all) {
                return true;
            } else if (!contains && all) {
                return false;
            }
        }
        return true;
    }
}
