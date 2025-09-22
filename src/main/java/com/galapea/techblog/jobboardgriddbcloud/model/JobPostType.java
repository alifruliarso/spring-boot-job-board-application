package com.galapea.techblog.jobboardgriddbcloud.model;

public enum JobPostType {
    FULL_TIME("Full Time"),
    PART_TIME("Part Time"),
    CONTRACT("Contract"),
    INTERNSHIP("Internship");

    private final String label;

    JobPostType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
