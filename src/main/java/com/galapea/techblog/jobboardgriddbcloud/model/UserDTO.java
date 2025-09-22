package com.galapea.techblog.jobboardgriddbcloud.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@UserRecruiterCompanyValid
public class UserDTO {

    @Size(max = 255)
    @UserIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    @UserEmailUnique
    private String email;

    @NotNull
    @Size(max = 255)
    private String fullName;

    @NotNull private UserRole role;

    @Size(max = 255)
    private String companyId;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(final UserRole role) {
        this.role = role;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(final String companyId) {
        this.companyId = companyId;
    }
}
