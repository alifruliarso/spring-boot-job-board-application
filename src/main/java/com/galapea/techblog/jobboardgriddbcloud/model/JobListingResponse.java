package com.galapea.techblog.jobboardgriddbcloud.model;

import java.time.LocalDateTime;

public record JobListingResponse(
        String id,
        String title,
        String description,
        String jobType,
        Double maximumMonthlySalary,
        LocalDateTime datePosted,
        String company,
        String workModel,
        String location,
        String applyUrl,
        String companyId) {
    public record CompanyResponse(String id, String name, String websiteUrl, String description) {}
}
