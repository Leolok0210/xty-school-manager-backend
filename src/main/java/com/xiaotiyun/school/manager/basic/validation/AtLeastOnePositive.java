package com.xiaotiyun.school.manager.basic.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOnePositiveValidator.class)
public @interface AtLeastOnePositive {
    String message() default "至少需要有一个正数值";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fields();
} 