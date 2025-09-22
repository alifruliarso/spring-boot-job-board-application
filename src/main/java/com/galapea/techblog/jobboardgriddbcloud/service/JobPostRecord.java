package com.galapea.techblog.jobboardgriddbcloud.service;

import java.time.LocalDateTime;

import com.galapea.techblog.jobboardgriddbcloud.model.JobPostType;
import com.galapea.techblog.jobboardgriddbcloud.model.WorkModel;

/**
 * A record representing a row in the PFTJobPost GridDB container.
 */
public record JobPostRecord(
        String id,
        String title,
        String description,
        JobPostType jobType,
        Double maximumMonthlySalary,
        LocalDateTime datePosted,
        String companyId,
        WorkModel workModel,
        String location,
        String applyUrl) {}
