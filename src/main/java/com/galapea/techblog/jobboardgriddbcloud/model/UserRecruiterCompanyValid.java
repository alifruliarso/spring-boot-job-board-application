package com.galapea.techblog.jobboardgriddbcloud.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom validation annotation to ensure that if a user's role is RECRUITER,
 * then the companyId field must be non-null and non-empty.
 */
@Documented
@Constraint(validatedBy = UserRecruiterCompanyValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRecruiterCompanyValid {
    String message() default "Recruiter must have a non-empty companyId";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
