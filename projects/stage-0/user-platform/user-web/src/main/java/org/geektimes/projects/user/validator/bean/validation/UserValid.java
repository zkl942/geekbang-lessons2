package org.geektimes.projects.user.validator.bean.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)  // for class
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidAnnotationValidator.class)
public @interface UserValid {

    String message() default "密码：6-32 位 电话号码: 采用中国大陆方式（11 位校验）";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    int pwFloor() default 0;

    int pwCeiling() default 0;

    int phonenolength() default 0;
}
