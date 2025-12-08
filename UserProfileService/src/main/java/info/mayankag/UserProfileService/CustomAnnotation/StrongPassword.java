package info.mayankag.UserProfileService.CustomAnnotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {

    // Default error message
    String message() default "Password must be 8-24 chars long, with 1 uppercase, 1 lowercase, 1 digit, and 1 special character";

    // Boilerplate code required by the standard
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
