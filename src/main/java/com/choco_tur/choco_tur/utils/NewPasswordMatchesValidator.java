package com.choco_tur.choco_tur.utils;

import com.choco_tur.choco_tur.web.dto.ChangePasswordDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NewPasswordMatchesValidator
  implements ConstraintValidator<NewPasswordMatches, Object> {
    
    @Override
    public void initialize(NewPasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        ChangePasswordDto changePasswordDto = (ChangePasswordDto) obj;
        return changePasswordDto.getPassword().equals(changePasswordDto.getMatchingPassword());
    }
}