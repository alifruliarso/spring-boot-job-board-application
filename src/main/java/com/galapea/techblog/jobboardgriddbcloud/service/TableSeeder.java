package com.galapea.techblog.jobboardgriddbcloud.service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galapea.techblog.jobboardgriddbcloud.model.CompanyDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.JobPostDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.JobPostType;
import com.galapea.techblog.jobboardgriddbcloud.model.SkillTagDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.UserDTO;
import com.galapea.techblog.jobboardgriddbcloud.model.UserRole;
import com.galapea.techblog.jobboardgriddbcloud.model.WorkModel;

@Component
public class TableSeeder implements CommandLineRunner {

    private static final String COM_XYZ = "com_xyz";
    public static final String DUMMY_USER1_FULLNAME = "John Doe";
    public static final String DUMMY_USER1_EMAIL = "j@doe.com";

    private final UserGridDbService userService;
    private final CompanyGridDbService companyService;
    private final SkillTagGridDbService skillTagService;
    private final JobPostGridDbService jobPostService;
    private final JobPostSkillGridDbService jobPostSkillService;

    public TableSeeder(
            final UserGridDbService userService,
            final CompanyGridDbService companyService,
            final SkillTagGridDbService skillTagService,
            final JobPostGridDbService jobPostService,
            final JobPostSkillGridDbService jobPostSkillService) {
        this.userService = userService;
        this.companyService = companyService;
        this.skillTagService = skillTagService;
        this.jobPostService = jobPostService;
        this.jobPostSkillService = jobPostSkillService;
    }

    @Override
    public void run(final String... args) throws IOException {
        // Create tables for all entities in a dependency-safe order
        companyService.createTable();
        userService.createTable();
        skillTagService.createTable();
        jobPostService.createTable();
        jobPostSkillService.createTable();

        CompanyDTO company = new CompanyDTO();
        company.setId("com_0mrcfv5xnafc5");
        company.setName("Default Company");
        company.setDescription("A technology blog publisher");
        company.setWebsiteUrl("www.google.com");
        CompanyDTO sisterCompany = new CompanyDTO();
        sisterCompany.setId("com_0mrcfv6c1ad1x");
        sisterCompany.setName("Sister Company");
        sisterCompany.setDescription("Another technology blog publisher");
        sisterCompany.setWebsiteUrl("www.bing.com");
        CompanyDTO theXyzCompany = new CompanyDTO();
        theXyzCompany.setId(COM_XYZ);
        theXyzCompany.setName("The XYZ Company");
        theXyzCompany.setDescription(
                "The XYZ Company is a fictional company used for demonstration purposes.");
        theXyzCompany.setWebsiteUrl("www.xyz.com");
        companyService.createAll(List.of(company, sisterCompany, theXyzCompany));

        UserDTO admin = new UserDTO();
        admin.setEmail("admin@jb.com");
        admin.setFullName("JB Administrator");
        admin.setRole(UserRole.ADMIN);
        if (!userService.emailExists(admin.getEmail())) {
            userService.create(admin);
        }
        UserDTO user = new UserDTO();
        user.setEmail("a@a.com");
        user.setFullName("Mr. A A");
        user.setRole(UserRole.APPLICANT);
        if (!userService.emailExists(user.getEmail())) {
            userService.create(user);
        }
        seedSkillTags();
    }

    private void seedSkillTags() throws IOException {
        ClassPathResource resource = new ClassPathResource("skills.json");
        Path path = Path.of(resource.getURI());
        ObjectMapper objectMapper = new ObjectMapper();
        List<SkillTagDTO> skillTags =
                List.of(objectMapper.readValue(path.toFile(), SkillTagDTO[].class));
        skillTagService.createAll(skillTags);
    }

    public void seedJobs() {
        JobPostDTO job = new JobPostDTO();
        job.setId("job_x0123");
        job.setCompanyId(COM_XYZ);
        job.setTitle("Senior Software Engineer (Java)");
        // @formatter:off
        var jobDesc =
                """
                What You Will Do

                Write efficient Java code to interact with various distributed databases and optimize performance.
                Collaborate with platform and infrastructure teams to ensure seamless integration and operation of distributed systems.
                Troubleshoot performance bottlenecks and work on mission critical systems to ensure availability in production environments.
                You will be fully responsible from its initial design to bringing new functionality live
                Identify and improve parts of the platform to make it more robust and scalable
                Collaborate across teams and time zones to make things happen, review code and be open to feedback

                Who You Are

                Advanced knowledge of Java, with hands-on experience in writing scalable and high-performing code for distributed systems.
                Experience working with distributed data stores like Cassandra, Redis or similar technologies.
                Experience with streaming technologies (e.g., Kafka, RabbitMQ) is required.
                You are comfortable with CI/CD pipelines (e.g., Jenkins, GitLab CI) and monitoring tools (e.g., Prometheus, Grafana, Kibana).
                You demonstrate excellent collaboration and communication skills, thriving in a team-oriented environment.
                You are a proactive problem solver, willing to take ownership of challenges. A team player with a strong desire to learn and improve continuously.
                Participate in an office hours on-call rotation once a month, responding to and resolving urgent issues to maintain system stability and minimize downtime.
                Nice to have, experience with public cloud providers
            """;
        // @formatter:on
        job.setDescription(jobDesc);
        job.setJobType(JobPostType.FULL_TIME);
        job.setWorkModel(WorkModel.ONSITE);
        job.setLocation("Amsterdam");
        job.setDatePosted(LocalDateTime.now());
        job.setApplyUrl("https://job-boards.greenhouse.io/adyen/jobs/7137129?");
        job.setMaximumMonthlySalary(Double.valueOf(25000));
        JobPostDTO job2 = new JobPostDTO();
        job2.setId("job_x345");
        job2.setCompanyId(COM_XYZ);
        job2.setTitle("AI Software Engineer (Remote)");
        // @formatter:off
        var jobDesc2 =
                """
                What You’ll Do
                Work with product and data teams to define machine learning goals and strategies

                Build and deploy ML models and tools that support personalization, automation, and insight generation

                Handle data pre processing, feature engineering, and model training/evaluation

                Help develop scalable ML infrastructure and pipelines

                Integrate ML outputs into products and user-facing features

                Stay updated on the latest AI trends and apply them as relevant

                Support debugging, optimization, and system maintenance

                Requirements
                Bachelor’s degree in Computer Science, Data Science, Engineering, or a related field

                2-4 years of experience in AI or backend software development

                Strong Python skills is a MUST with experience in frameworks like TensorFlow, PyTorch, or Scikit-learn

                Understanding of key ML workflows: data cleaning, model building, evaluation, and tuning

                Familiarity with API development or model deployment in real-world environments

                Experience working with tools like Google Colab, Jupyter, or cloud ML platforms

                Strong problem-solving and communication skills
            """;
        // @formatter:on
        job2.setDescription(jobDesc2);
        job2.setJobType(JobPostType.FULL_TIME);
        job2.setWorkModel(WorkModel.REMOTE);
        job2.setLocation("Indonesia");
        job2.setDatePosted(LocalDateTime.now());
        job2.setApplyUrl(
                "https://jobs.ashbyhq.com/bjakcareer/7e13c3b2-3aa7-4bf3-a76e-2331f349ccc9");
        job2.setMaximumMonthlySalary(Double.valueOf(25000));
        jobPostService.createAll(List.of(job, job2));
    }
}
