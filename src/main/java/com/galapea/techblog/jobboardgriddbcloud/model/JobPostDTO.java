package com.galapea.techblog.jobboardgriddbcloud.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobPostDTO {

    private String id;

    @NotNull
    @Size(max = 255)
    private String title;

    @NotNull private String description;

    @NotNull private JobPostType jobType;

    @NotNull private Double maximumMonthlySalary;

    @NotNull private LocalDateTime datePosted;

    @NotNull
    @Size(max = 255)
    private String companyId;

    @NotNull private WorkModel workModel;

    @Size(max = 255)
    private String location;

    @Size(max = 255)
    private String applyUrl;

    private List<SkillTagDTO> skills;

    private List<String> skillsIds;

    public List<String> getSkillsIds() {
        return skillsIds;
    }

    public void setSkillsIds(List<String> skillsIds) {
        this.skillsIds = skillsIds;
    }

    public List<SkillTagDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillTagDTO> skills) {
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public JobPostType getJobType() {
        return jobType;
    }

    public void setJobType(final JobPostType jobType) {
        this.jobType = jobType;
    }

    public Double getMaximumMonthlySalary() {
        return maximumMonthlySalary;
    }

    public void setMaximumMonthlySalary(final Double maximumMonthlySalary) {
        this.maximumMonthlySalary = maximumMonthlySalary;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(final LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(final String companyId) {
        this.companyId = companyId;
    }

    public WorkModel getWorkModel() {
        return workModel;
    }

    public void setWorkModel(final WorkModel workModel) {
        this.workModel = workModel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(final String applyUrl) {
        this.applyUrl = applyUrl;
    }
}
