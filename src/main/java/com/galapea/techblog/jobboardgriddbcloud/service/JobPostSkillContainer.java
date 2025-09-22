package com.galapea.techblog.jobboardgriddbcloud.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbCloudClient;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbCloudSQLStmt;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbColumn;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbContainerDefinition;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbException;
import com.galapea.techblog.jobboardgriddbcloud.webapi.SQLSelectResponse;
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsRequest;
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsResponse;

@Component
public class JobPostSkillContainer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbCloudClient gridDbCloudClient;
    private static final String TBL_NAME = "JBJobPostSkill";

    public JobPostSkillContainer(GridDbCloudClient gridDbCloudClient) {
        this.gridDbCloudClient = gridDbCloudClient;
    }

    public void createTable() {
        log.info("Creating table " + TBL_NAME + " in GridDB...");
        List<GridDbColumn> columns =
                List.of(
                        new GridDbColumn("id", "STRING", Set.of("TREE")),
                        new GridDbColumn("jobPostId", "STRING", Set.of("TREE")),
                        new GridDbColumn("skillTagId", "STRING", Set.of("TREE")));

        GridDbContainerDefinition containerDefinition =
                GridDbContainerDefinition.build(TBL_NAME, columns);
        this.gridDbCloudClient.createContainer(containerDefinition);
        log.info("Created table " + TBL_NAME + " with columns: {}", columns);
    }

    private void post(String uri, Object body) {
        try {
            this.gridDbCloudClient.post(uri, body);
        } catch (GridDbException e) {
            throw e;
        } catch (Exception e) {
            throw new GridDbException(
                    "Failed to execute POST request",
                    HttpStatusCode.valueOf(500),
                    e.getMessage(),
                    e);
        }
    }

    private String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "\\'").replace("\\", "\\\\");
    }

    public void insert(JobPostSkillRecord jobPostSkill) {
        String stmt =
                "INSERT INTO "
                        + TBL_NAME
                        + "(id, jobPostId, skillTagId) VALUES ("
                        + "'"
                        + escapeString(jobPostSkill.id())
                        + "', "
                        + "'"
                        + escapeString(jobPostSkill.jobPostId())
                        + "', "
                        + "'"
                        + escapeString(jobPostSkill.skillTagId())
                        + "'"
                        + ")";
        GridDbCloudSQLStmt insert = new GridDbCloudSQLStmt(stmt);

        post("/sql/update", List.of(insert));
    }

    public void saveRecords(List<JobPostSkillRecord> jobPostSkillRecords) {
        // Initialize a StringBuilder to create the JSON-like array representation
        StringBuilder sb = new StringBuilder();
        sb.append("["); // Start the outer array
        for (int i = 0; i < jobPostSkillRecords.size(); i++) {
            JobPostSkillRecord record = jobPostSkillRecords.get(i);
            sb.append("["); // Start the inner array (each record)
            // Append each record field in order
            sb.append("\"").append(record.id()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.jobPostId()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.skillTagId()).append("\"");
            sb.append("]"); // End the inner array
            // Add a comma between record, except for the last one
            if (i < jobPostSkillRecords.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]"); // End the outer array
        String result = sb.toString();
        log.info("JobPostSkills array: {}", result);
        this.gridDbCloudClient.registerRows(TBL_NAME, result);
    }

    public List<JobPostSkillRecord> getAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<JobPostSkillRecord> jobPostSkills = convertResponseToRecord(response);
        log.info("Fetched {} job post skills from GridDB", jobPostSkills.size());
        return jobPostSkills;
    }

    public Optional<JobPostSkillRecord> getOne(String id) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(1L).condition("id == \'" + id + "\'").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<JobPostSkillRecord> jobPostSkills = convertResponseToRecord(response);
        log.info("Fetched {} job post skills from GridDB", jobPostSkills.size());
        return jobPostSkills.isEmpty() ? Optional.empty() : Optional.of(jobPostSkills.get(0));
    }

    public List<JobPostSkillRecord> getByJobPostId(String jobPostId) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(50L)
                        .condition("jobPostId == \'" + jobPostId + "\'")
                        .sort("id ASC")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<JobPostSkillRecord> jobPostSkills = convertResponseToRecord(response);
        log.info(
                "Fetched {} job post skills for job post {} from GridDB",
                jobPostSkills.size(),
                jobPostId);
        return jobPostSkills;
    }

    public List<JobPostSkillRecord> getBySkillTagId(String skillTagId) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(50L)
                        .condition("skillTagId == \'" + skillTagId + "\'")
                        .sort("id ASC")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<JobPostSkillRecord> jobPostSkills = convertResponseToRecord(response);
        log.info(
                "Fetched {} job post skills for skill tag {} from GridDB",
                jobPostSkills.size(),
                skillTagId);
        return jobPostSkills;
    }

    public void deleteByJobPostId(String jobPostId) {
        String stmt =
                "DELETE FROM " + TBL_NAME + " WHERE jobPostId = '" + escapeString(jobPostId) + "'";
        GridDbCloudSQLStmt delete = new GridDbCloudSQLStmt(stmt);
        post("/sql/update", List.of(delete));
        log.info("Deleted job post skills for job post: {}", jobPostId);
    }

    public void deleteBySkillTagId(String skillTagId) {
        String stmt =
                "DELETE FROM "
                        + TBL_NAME
                        + " WHERE skillTagId = '"
                        + escapeString(skillTagId)
                        + "'";
        GridDbCloudSQLStmt delete = new GridDbCloudSQLStmt(stmt);
        post("/sql/update", List.of(delete));
        log.info("Deleted job post skills for skill tag: {}", skillTagId);
    }

    private List<JobPostSkillRecord> convertResponseToRecord(AcquireRowsResponse response) {
        List<JobPostSkillRecord> results =
                response.getRows().stream()
                        .map(
                                row -> {
                                    try {
                                        String id = row.get(0).toString();
                                        String jobPostId = row.get(1).toString();
                                        String skillTagId = row.get(2).toString();

                                        return new JobPostSkillRecord(
                                                id, jobPostId, skillTagId, "-");
                                    } catch (Exception e) {
                                        log.error(
                                                "Error parsing job post skill row: {}. Error: {}",
                                                row.toString(),
                                                e.getMessage());
                                        return null;
                                    }
                                })
                        .filter(r -> r != null)
                        .collect(Collectors.toList());
        return results;
    }

    public List<JobPostSkillRecord> getSkillsByJobId(String jobId) {
        String stmt2 =
                """
            SELECT \
                jps.id AS id, st.name AS name, st.id AS skillTagId \
            FROM \
                JBSkillTag st \
            JOIN \
                JBJobPostSkill jps ON st.id = jps.skillTagId \
            WHERE \
                jps.jobPostId = '%s'"""
                        .formatted(jobId);

        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt2));
        SQLSelectResponse[] response = this.gridDbCloudClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Failed to fetch job skill. Response is null or size mismatch. Expected: {}, Actual: {}",
                    statementList.size(),
                    response != null ? response.length : 0);
            return List.of();
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for {}", jobId);
            return List.of();
        }
        List<JobPostSkillRecord> records =
                results.stream()
                        .map(
                                row -> {
                                    try {
                                        String id = row.get(0).toString();
                                        String skillName = row.get(1).toString();
                                        String skillTagId = row.get(2).toString();
                                        return new JobPostSkillRecord(
                                                id, jobId, skillTagId, skillName);
                                    } catch (Exception e) {
                                        log.error(
                                                "Error parsing transaction summary row: {}. Error: {}",
                                                row.toString(),
                                                e.getMessage());
                                        return null;
                                    }
                                })
                        .filter(s -> s != null)
                        .collect(Collectors.toList());
        log.info("Fetched {} records job: {}", records.size(), jobId);
        return records;
    }
}
