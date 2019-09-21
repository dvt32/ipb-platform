package com.ipb.platform.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * This annotation is used 
 * as a way to check if a user's two password field values match.
 * 
 * The actual implementation of the validation
 * is in the class specified in the @Constraint annotation below.
 * 
 * @author dvt32
 */
@Target({TYPE, ANNOTATION_TYPE}) 
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches { 
    String message() default "Passwords don't match!";
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {};
}