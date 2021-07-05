package com.jwt.seckill.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =
        {com.jwt.seckill.validator.MobileValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
public @interface Mobile {
    boolean required() default true;
    String message() default "非法手机号";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
