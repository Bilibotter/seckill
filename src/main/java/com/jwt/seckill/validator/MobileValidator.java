package com.jwt.seckill.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class MobileValidator implements ConstraintValidator<Mobile, String> {
    private boolean required = false;
    private static final Pattern mobilePattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    @Override
    public void initialize(Mobile mobile) {
        required = mobile.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!required && s.isEmpty()) {
            return true;
        }
        return mobilePattern.matcher(s).matches();
    }
}
