package com.galapea.techblog.jobboardgriddbcloud.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    RECRUITER,
    ADMIN,
    APPLICANT;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
