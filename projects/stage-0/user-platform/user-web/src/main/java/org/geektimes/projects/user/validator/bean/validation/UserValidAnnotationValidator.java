package org.geektimes.projects.user.validator.bean.validation;

import org.geektimes.projects.user.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int pwFloor;
    private int pwCeiling;
    private int phonenolength;

    public void initialize(UserValid annotation) {
        this.pwFloor = annotation.pwFloor();
        this.pwCeiling = annotation.pwCeiling();
        this.phonenolength = annotation.phonenolength();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {
        int pwLength = value.getPassword().length();
        if (pwLength < pwFloor || pwLength > pwCeiling) return false;
        if (value.getPhoneNumber().length() != 11) return false;

        return true;
    }
}
