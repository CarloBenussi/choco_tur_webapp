package com.choco_tur.choco_tur.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.choco_tur.choco_tur.web.dto.BusinessRegistrationDto;
import com.choco_tur.choco_tur.web.dto.UserRegistrationDto;

public class PasswordMatchesValidator
  implements ConstraintValidator<PasswordMatches, Object> {
    
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        String password = "";
        String matchingPassword = " ";
        if (obj instanceof UserRegistrationDto) {
          UserRegistrationDto user = (UserRegistrationDto) obj;
          password = user.getPassword();
          matchingPassword = user.getMatchingPassword();
        } else if (obj instanceof BusinessRegistrationDto) {
          BusinessRegistrationDto business = (BusinessRegistrationDto) obj;
          password = business.getPassword();
          matchingPassword = business.getMatchingPassword();
        }

        return password.equals(matchingPassword);
    }
}