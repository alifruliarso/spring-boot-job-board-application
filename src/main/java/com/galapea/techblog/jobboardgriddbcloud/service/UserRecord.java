package com.galapea.techblog.jobboardgriddbcloud.service;

import com.galapea.techblog.jobboardgriddbcloud.model.UserRole;

/**
 * A record representing a row in the User GridDB container.
 */
public record UserRecord(
        String id, String email, String fullName, UserRole role, String companyId) {}
