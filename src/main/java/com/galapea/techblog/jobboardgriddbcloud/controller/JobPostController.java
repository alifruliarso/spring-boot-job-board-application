package com.galapea.techblog.jobboardgriddbcloud.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galapea.techblog.jobboardgriddbcloud.model.CompanyDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.JobPostDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.JobPostType;
import com.galapea.techblog.jobboardgriddbcloud.model.SkillTagDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.WorkModel;
import com.galapea.techblog.jobboardgriddbcloud.service.CompanyGridDbService;
import com.galapea.techblog.jobboardgriddbcloud.service.JobPostGridDbService;
import com.galapea.techblog.jobboardgriddbcloud.service.JobPostSkillGridDbService;
import com.galapea.techblog.jobboardgriddbcloud.service.SkillTagGridDbService;
import com.galapea.techblog.jobboardgriddbcloud.service.TableSeeder;
import com.galapea.techblog.jobboardgriddbcloud.util.DateTimeUtil;
import com.galapea.techblog.jobboardgriddbcloud.util.WebUtils;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/jobs")
public class JobPostController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JobPostGridDbService jobPostService;
    private final CompanyGridDbService companyService;
    private final JobPostSkillGridDbService jobPostSkillService;
    private final SkillTagGridDbService skillTagService;
    private final ChatModel chatModel;
    private final TableSeeder tableSeeder;

    private final Map<String, String> jobTypeValues =
            Arrays.stream(JobPostType.values())
                    .collect(
                            java.util.stream.Collectors.toMap(
                                    JobPostType::name, JobPostType::getLabel));

    public JobPostController(
            final JobPostGridDbService jobPostService,
            final CompanyGridDbService companyService,
            final JobPostSkillGridDbService jobPostSkillService,
            final SkillTagGridDbService skillTagService,
            ChatModel chatModel,
            TableSeeder tableSeeder) {
        this.jobPostService = jobPostService;
        this.companyService = companyService;
        this.jobPostSkillService = jobPostSkillService;
        this.skillTagService = skillTagService;
        this.chatModel = chatModel;
        this.tableSeeder = tableSeeder;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        Map<String, String> companies =
                companyService.findAll().stream()
                        .collect(
                                java.util.stream.Collectors.toMap(
                                        com -> com.getId(), com -> com.getName()));
        model.addAttribute("jobTypeValues", jobTypeValues);
        model.addAttribute("workModelValues", WorkModel.values());
        model.addAttribute("companyIdValues", companies);
    }

    @GetMapping
    public String list(
            @RequestParam(name = "searchSkill", required = false) String searchSkill,
            final Model model) {
        List<JobPostDTO> jobs = jobPostService.findAll(searchSkill);
        List<JobListingResponse> jobPosts =
                jobs.stream()
                        .map(
                                jobPost -> {
                                    JobListingResponse response =
                                            buildJobPostResponse(jobPost.getId());
                                    return response;
                                })
                        .toList();
        model.addAttribute("jobPosts", jobPosts);
        model.addAttribute("searchSkill", searchSkill);
        return "jobs/list";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(@ModelAttribute("jobPost") final JobPostDTO jobPostDTO) {
        return "jobs/add";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String add(
            @ModelAttribute("jobPost") @Valid final JobPostDTO jobPostDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobs/add";
        }
        jobPostService.create(jobPostDTO);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobPost.create.success"));
        return "redirect:/jobs";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable(name = "id") final String id, final Model model) {
        JobPostDTO jobPostDTO = jobPostService.get(id);
        List<SkillTagDTO> skillTags =
                jobPostSkillService.findByJobPostId(id).stream()
                        .map(
                                js -> {
                                    SkillTagDTO stag = new SkillTagDTO();
                                    stag.setId(js.getSkillTagId());
                                    stag.setName(js.getSkillName());
                                    return stag;
                                })
                        .collect(Collectors.toList());
        jobPostDTO.setSkills(skillTags);
        model.addAttribute("jobPost", jobPostDTO);
        return "jobs/edit";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(
            @PathVariable(name = "id") final String id,
            @ModelAttribute("jobPost") @Valid final JobPostDTO jobPostDTO,
            final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "jobs/edit";
        }
        jobPostService.update(id, jobPostDTO);
        jobPostSkillService.replaceSkillsForJobPost(id, jobPostDTO.getSkillsIds());
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_SUCCESS, WebUtils.getMessage("jobPost.update.success"));
        return "redirect:/jobs";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(
            @PathVariable(name = "id") final String id,
            final RedirectAttributes redirectAttributes) {
        jobPostService.delete(id);
        redirectAttributes.addFlashAttribute(
                WebUtils.MSG_INFO, WebUtils.getMessage("jobPost.delete.success"));
        return "redirect:/jobs";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable(name = "id") final String id, final Model model) {
        JobListingResponse jobPost = buildJobPostResponse(id);
        model.addAttribute("jobPost", jobPost);
        return "jobs/view";
    }

    @GetMapping("/skills/extract/{id}")
    public String extractSkill(@PathVariable(name = "id") final String jobId, final Model model)
            throws JsonProcessingException {
        JobPostDTO jobPostDTO = jobPostService.get(jobId);
        List<SkillTagDTO> skillTags = skillTagService.findAll(200L);
        List<SkillTagDTO> extractedSkills = generateSkills(jobPostDTO, skillTags);
        model.addAttribute("extractedSkills", extractedSkills);
        return "fragments/skills :: skillsDiv";
    }

    private List<SkillTagDTO> generateSkills(JobPostDTO jobPostDTO, List<SkillTagDTO> skillTags)
            throws JsonProcessingException, JsonMappingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String skillCatalogJson = objectMapper.writeValueAsString(skillTags);
        BeanOutputConverter<SkillResponse> outputConverter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<SkillResponse>() {});
        String format = outputConverter.getFormat();
        // @formatter:off
        String promptStr =
                """
                You are an AI assistant that extracts required skills from a job description.
                TASK:
                - Only return skills present in the provided JSON skill catalog.
                - Matching is case-insensitive.
                - Do not invent or include skills not in the catalog.
                - Output strictly as a JSON array of objects.

                NOW PROCESS:
                <JOB_DESCRIPTION>
                {jobDescription}
                </JOB_DESCRIPTION>
                <SKILL_LIST>
                {skillCatalog}
                </SKILL_LIST>

                {format}
            """;
        // @formatter:on
        Prompt prompt =
                PromptTemplate.builder()
                        .template(promptStr)
                        .build()
                        .create(
                                Map.of(
                                        "jobDescription",
                                        jobPostDTO.getDescription(),
                                        "skillCatalog",
                                        skillCatalogJson,
                                        "format",
                                        format),
                                OpenAiChatOptions.builder()
                                        .responseFormat(
                                                new ResponseFormat(
                                                        ResponseFormat.Type.JSON_OBJECT, null))
                                        .build());
        var generation = this.chatModel.call(prompt).getResult();
        String outputText = generation.getOutput().getText();
        log.info(">>>>= AI outputText:  {}", outputText);
        SkillResponse skillResponse = outputConverter.convert(outputText);
        return skillResponse.skills();
    }

    record SkillResponse(List<SkillTagDTO> skills) {}

    private JobListingResponse buildJobPostResponse(final String id) {
        List<String> skills =
                jobPostSkillService.findByJobPostId(id).stream()
                        .map(s -> s.getSkillName())
                        .toList();
        JobPostDTO jobPostDTO = jobPostService.get(id);
        CompanyDTO companyDTO = companyService.get(jobPostDTO.getCompanyId());
        JobListingResponse jobPost =
                new JobListingResponse(
                        jobPostDTO.getId(),
                        jobPostDTO.getTitle(),
                        jobPostDTO.getDescription(),
                        jobPostDTO.getJobType().getLabel(),
                        jobPostDTO.getMaximumMonthlySalary(),
                        jobPostDTO.getDatePosted(),
                        jobPostDTO.getWorkModel().name(),
                        jobPostDTO.getLocation(),
                        jobPostDTO.getApplyUrl(),
                        DateTimeUtil.formatDaysAgo(jobPostDTO.getDatePosted()),
                        skills,
                        new CompanyResponse(
                                jobPostDTO.getCompanyId(),
                                companyDTO.getName(),
                                companyDTO.getWebsiteUrl(),
                                companyDTO.getDescription()));
        return jobPost;
    }

    @GetMapping("/generateDemoData")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateDemoData(final RedirectAttributes redirectAttributes) {
        this.tableSeeder.seedJobs();
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, "Finished creating demo jobs.");
        return "redirect:/jobs";
    }
}

record JobListingResponse(
        String id,
        String title,
        String description,
        String jobType,
        Double maximumMonthlySalary,
        LocalDateTime datePosted,
        String workModel,
        String location,
        String applyUrl,
        String datePostedFormatted,
        List<String> skills,
        CompanyResponse company) {}

record CompanyResponse(String id, String name, String websiteUrl, String description) {}
