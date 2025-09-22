package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.galapea.techblog.jobboardgriddbcloud.model.JobPostSkillDTO;
import com.galapea.techblog.jobboardgriddbcloud.util.NotFoundException;
import com.galapea.techblog.jobboardgriddbcloud.util.NotImplementedException;
import com.github.f4b6a3.tsid.TsidCreator;

@Service
public class JobPostSkillGridDbService {

    private final JobPostSkillContainer jobPostSkillContainer;

    public JobPostSkillGridDbService(JobPostSkillContainer jobPostSkillContainer) {
        this.jobPostSkillContainer = jobPostSkillContainer;
    }

    public static String nextId() {
        return TsidCreator.getTsid().format("jps_%s");
    }

    public List<JobPostSkillDTO> findAll() {
        final List<JobPostSkillRecord> jobPostSkills = jobPostSkillContainer.getAll();
        return jobPostSkills.stream()
                .map(jobPostSkill -> mapToDTO(jobPostSkill, new JobPostSkillDTO()))
                .collect(Collectors.toList());
    }

    public JobPostSkillDTO get(final String id) {
        return jobPostSkillContainer
                .getOne(id)
                .map(jobPostSkill -> mapToDTO(jobPostSkill, new JobPostSkillDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<JobPostSkillDTO> findByJobPostId(final String jobPostId) {
        final List<JobPostSkillRecord> jobPostSkills =
                jobPostSkillContainer.getSkillsByJobId(jobPostId);
        return jobPostSkills.stream()
                .map(jobPostSkill -> mapToDTO(jobPostSkill, new JobPostSkillDTO()))
                .collect(Collectors.toList());
    }

    public List<JobPostSkillDTO> findBySkillTagId(final String skillTagId) {
        final List<JobPostSkillRecord> jobPostSkills =
                jobPostSkillContainer.getBySkillTagId(skillTagId);
        return jobPostSkills.stream()
                .map(jobPostSkill -> mapToDTO(jobPostSkill, new JobPostSkillDTO()))
                .collect(Collectors.toList());
    }

    public String create(final JobPostSkillDTO jobPostSkillDTO) {
        String id = (jobPostSkillDTO.getId() != null) ? jobPostSkillDTO.getId() : nextId();
        JobPostSkillRecord newJobPostSkill =
                new JobPostSkillRecord(
                        id, jobPostSkillDTO.getJobPostId(), jobPostSkillDTO.getSkillTagId(), "");
        jobPostSkillContainer.saveRecords(List.of(newJobPostSkill));
        return id;
    }

    public void createAll(List<JobPostSkillDTO> jobPostSkillDTOs) {
        List<JobPostSkillRecord> jobPostSkillRecords =
                jobPostSkillDTOs.stream()
                        .map(
                                jobPostSkillDTO ->
                                        new JobPostSkillRecord(
                                                (jobPostSkillDTO.getId() != null)
                                                        ? jobPostSkillDTO.getId()
                                                        : nextId(),
                                                jobPostSkillDTO.getJobPostId(),
                                                jobPostSkillDTO.getSkillTagId(),
                                                ""))
                        .collect(Collectors.toList());
        if (!jobPostSkillRecords.isEmpty()) {
            jobPostSkillContainer.saveRecords(jobPostSkillRecords);
        }
    }

    public void createSkillsForJobPost(String jobPostId, List<String> skillTagIds) {
        List<JobPostSkillDTO> jobPostSkills =
                skillTagIds.stream()
                        .map(
                                skillTagId -> {
                                    JobPostSkillDTO dto = new JobPostSkillDTO();
                                    dto.setJobPostId(jobPostId);
                                    dto.setSkillTagId(skillTagId);
                                    return dto;
                                })
                        .collect(Collectors.toList());
        createAll(jobPostSkills);
    }

    public void update(final String id, final JobPostSkillDTO jobPostSkillDTO) {
        JobPostSkillRecord updatedJobPostSkill =
                new JobPostSkillRecord(
                        id, jobPostSkillDTO.getJobPostId(), jobPostSkillDTO.getSkillTagId(), "");
        jobPostSkillContainer.saveRecords(List.of(updatedJobPostSkill));
    }

    public void delete(final String id) {
        throw new NotImplementedException("Delete operation is not implemented yet.");
    }

    public void deleteByJobPostId(final String jobPostId) {
        jobPostSkillContainer.deleteByJobPostId(jobPostId);
    }

    public void deleteBySkillTagId(final String skillTagId) {
        jobPostSkillContainer.deleteBySkillTagId(skillTagId);
    }

    public void replaceSkillsForJobPost(String jobPostId, List<String> skillTagIds) {
        // First delete existing skills for the job post
        deleteByJobPostId(jobPostId);
        // Then create new skills
        if (!skillTagIds.isEmpty()) {
            createSkillsForJobPost(jobPostId, skillTagIds);
        }
    }

    private JobPostSkillDTO mapToDTO(
            final JobPostSkillRecord jobPostSkill, final JobPostSkillDTO jobPostSkillDTO) {
        jobPostSkillDTO.setId(jobPostSkill.id());
        jobPostSkillDTO.setJobPostId(jobPostSkill.jobPostId());
        jobPostSkillDTO.setSkillTagId(jobPostSkill.skillTagId());
        jobPostSkillDTO.setSkillName(jobPostSkill.skillName());
        return jobPostSkillDTO;
    }

    public boolean idExists(final String id) {
        return jobPostSkillContainer.getOne(id).isPresent();
    }

    public void createTable() {
        jobPostSkillContainer.createTable();
    }
}
