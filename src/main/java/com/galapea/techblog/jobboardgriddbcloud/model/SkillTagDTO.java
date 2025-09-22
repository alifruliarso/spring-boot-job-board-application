package com.galapea.techblog.jobboardgriddbcloud.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SkillTagDTO {

    @Size(max = 255)
    @SkillTagIdValid
    private String id;

    @NotNull
    @Size(max = 255)
    @SkillTagNameUnique
    private String name;

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
}
