package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.JobPostDTO;
import com.galapea.techblog.jobboardgriddbcloud.util.NotFoundException;
import com.galapea.techblog.jobboardgriddbcloud.util.NotImplementedException;
import com.github.f4b6a3.tsid.TsidCreator;

@Service
public class JobPostGridDbService {

    private final JobPostContainer jobPostContainer;

    public JobPostGridDbService(JobPostContainer jobPostContainer) {
        this.jobPostContainer = jobPostContainer;
    }

    public static String nextId() {
        return TsidCreator.getTsid().format("job_%s");
    }

    public List<JobPostDTO> findAll(String searchSkill) {
        final List<JobPostRecord> jobPosts;
        if (searchSkill != null && !searchSkill.isBlank()) {
            jobPosts = jobPostContainer.searchBySkill(searchSkill);
        } else {
            jobPosts = jobPostContainer.getAll();
        }
        return jobPosts.stream()
                .map(jobPost -> mapToDTO(jobPost, new JobPostDTO()))
                .collect(Collectors.toList());
    }

    public JobPostDTO get(final String id) {
        return jobPostContainer
                .getOne(id)
                .map(jobPost -> mapToDTO(jobPost, new JobPostDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<JobPostDTO> findByCompanyId(final String companyId) {
        final List<JobPostRecord> jobPosts = jobPostContainer.getByCompanyId(companyId);
        return jobPosts.stream()
                .map(jobPost -> mapToDTO(jobPost, new JobPostDTO()))
                .collect(Collectors.toList());
    }

    public String create(final JobPostDTO jobPostDTO) {
        String id = (jobPostDTO.getId() != null) ? jobPostDTO.getId() : nextId();
        JobPostRecord newJobPost =
                new JobPostRecord(
                        id,
                        jobPostDTO.getTitle(),
                        jobPostDTO.getDescription(),
                        jobPostDTO.getJobType(),
                        jobPostDTO.getMaximumMonthlySalary(),
                        jobPostDTO.getDatePosted(),
                        jobPostDTO.getCompanyId(),
                        jobPostDTO.getWorkModel(),
                        jobPostDTO.getLocation(),
                        jobPostDTO.getApplyUrl());
        jobPostContainer.saveRecords(List.of(newJobPost));
        return id;
    }

    public void createAll(List<JobPostDTO> jobPostDTOs) {
        List<JobPostRecord> jobPostRecords =
                jobPostDTOs.stream()
                        .map(
                                jobPostDTO ->
                                        new JobPostRecord(
                                                (jobPostDTO.getId() != null)
                                                        ? jobPostDTO.getId()
                                                        : nextId(),
                                                jobPostDTO.getTitle(),
                                                jobPostDTO.getDescription(),
                                                jobPostDTO.getJobType(),
                                                jobPostDTO.getMaximumMonthlySalary(),
                                                jobPostDTO.getDatePosted(),
                                                jobPostDTO.getCompanyId(),
                                                jobPostDTO.getWorkModel(),
                                                jobPostDTO.getLocation(),
                                                jobPostDTO.getApplyUrl()))
                        .collect(Collectors.toList());
        if (!jobPostRecords.isEmpty()) {
            jobPostContainer.saveRecords(jobPostRecords);
        }
    }

    public void update(final String id, final JobPostDTO jobPostDTO) {
        JobPostRecord updatedJobPost =
                new JobPostRecord(
                        id,
                        jobPostDTO.getTitle(),
                        jobPostDTO.getDescription(),
                        jobPostDTO.getJobType(),
                        jobPostDTO.getMaximumMonthlySalary(),
                        jobPostDTO.getDatePosted(),
                        jobPostDTO.getCompanyId(),
                        jobPostDTO.getWorkModel(),
                        jobPostDTO.getLocation(),
                        jobPostDTO.getApplyUrl());
        jobPostContainer.saveRecords(List.of(updatedJobPost));
    }

    public void delete(final String id) {
        throw new NotImplementedException("Delete operation is not implemented yet.");
    }

    private JobPostDTO mapToDTO(final JobPostRecord jobPost, final JobPostDTO jobPostDTO) {
        jobPostDTO.setId(jobPost.id());
        jobPostDTO.setTitle(jobPost.title());
        jobPostDTO.setDescription(jobPost.description());
        jobPostDTO.setJobType(jobPost.jobType());
        jobPostDTO.setMaximumMonthlySalary(jobPost.maximumMonthlySalary());
        jobPostDTO.setDatePosted(jobPost.datePosted());
        jobPostDTO.setCompanyId(jobPost.companyId());
        jobPostDTO.setWorkModel(jobPost.workModel());
        jobPostDTO.setLocation(jobPost.location());
        jobPostDTO.setApplyUrl(jobPost.applyUrl());
        return jobPostDTO;
    }

    public boolean idExists(final String id) {
        return jobPostContainer.getOne(id).isPresent();
    }

    public void createTable() {
        jobPostContainer.createTable();
    }
}
