package com.choco_tur.choco_tur.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE,ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = NewPasswordMatchesValidator.class)
@Documented
public @interface NewPasswordMatches {
    String message() default "New passwords don't match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}