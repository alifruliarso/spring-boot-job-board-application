package com.galapea.techblog.jobboardgriddbcloud.model;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserRecruiterCompanyValidator
        implements ConstraintValidator<UserRecruiterCompanyValid, UserDTO> {

    @Override
    public boolean isValid(UserDTO user, ConstraintValidatorContext context) {
        if (user == null) {
            return true;
        }
        if (UserRole.RECRUITER.equals(user.getRole())) {
            return user.getCompanyId() != null && !user.getCompanyId().trim().isEmpty();
        }
        return true;
    }
}
