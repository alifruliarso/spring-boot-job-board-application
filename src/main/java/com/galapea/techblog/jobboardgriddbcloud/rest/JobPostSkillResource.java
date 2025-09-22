package com.galapea.techblog.jobboardgriddbcloud.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.galapea.techblog.jobboardgriddbcloud.model.JobPostSkillDTO;
import com.galapea.techblog.jobboardgriddbcloud.service.JobPostSkillGridDbService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/api/jobPostSkills", produces = MediaType.APPLICATION_JSON_VALUE)
public class JobPostSkillResource {

    private final JobPostSkillGridDbService jobPostSkillService;

    public JobPostSkillResource(final JobPostSkillGridDbService jobPostSkillService) {
        this.jobPostSkillService = jobPostSkillService;
    }

    @GetMapping
    public ResponseEntity<List<JobPostSkillDTO>> getAllJobPostSkills() {
        return ResponseEntity.ok(jobPostSkillService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostSkillDTO> getJobPostSkill(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(jobPostSkillService.get(id));
    }

    @PostMapping
    public ResponseEntity<String> createJobPostSkill(
            @RequestBody @Valid final JobPostSkillDTO jobPostSkillDTO) {
        final String createdId = jobPostSkillService.create(jobPostSkillDTO);
        return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateJobPostSkill(
            @PathVariable(name = "id") final String id,
            @RequestBody @Valid final JobPostSkillDTO jobPostSkillDTO) {
        jobPostSkillService.update(id, jobPostSkillDTO);
        return ResponseEntity.ok('"' + id + '"');
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPostSkill(@PathVariable(name = "id") final String id) {
        jobPostSkillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
