package com.galapea.techblog.jobboardgriddbcloud.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobPostSkillDTO {

    @Size(max = 255)
    @JobPostSkillIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    private String jobPostId;

    @NotNull
    @Size(max = 255)
    private String skillTagId;

    private String skillName;

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(final String jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getSkillTagId() {
        return skillTagId;
    }

    public void setSkillTagId(final String skillTagId) {
        this.skillTagId = skillTagId;
    }
}
