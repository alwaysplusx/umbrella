package com.harmony.umbrella.validator.validators;

import java.text.ParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.harmony.umbrella.util.Formats;
import com.harmony.umbrella.validator.constraints.FormatDate;

/**
 * @author wuxii@foxmail.com
 */
public class FormatDateValidator implements ConstraintValidator<FormatDate, String> {

    private Formats.NullableDateFormat ndf;

    @Override
    public void initialize(FormatDate annotation) {
        this.ndf = Formats.createDateFormat(annotation.pattern());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        try {
            ndf.parseDate(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

}
