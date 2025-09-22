package com.galapea.techblog.jobboardgriddbcloud.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CompanyDTO {

    @Size(max = 255)
    @CompanyIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String websiteUrl;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(final String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
