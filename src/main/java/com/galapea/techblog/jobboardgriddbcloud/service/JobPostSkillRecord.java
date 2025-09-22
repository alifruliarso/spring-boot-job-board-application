package com.galapea.techblog.jobboardgriddbcloud.service;

/**
 * A record representing a row in the PFTJobPostSkill GridDB container.
 */
public record JobPostSkillRecord(
        String id, String jobPostId, String skillTagId, String skillName) {}
