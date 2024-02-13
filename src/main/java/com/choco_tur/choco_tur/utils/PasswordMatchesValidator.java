package com.choco_tur.choco_tur.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.choco_tur.choco_tur.web.dto.UserRegistrationDto;

public class PasswordMatchesValidator
  implements ConstraintValidator<PasswordMatches, Object> {
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserRegistrationDto user = (UserRegistrationDto) obj;
        return user.getPassword().equals(user.getMatchingPassword());
    }
}