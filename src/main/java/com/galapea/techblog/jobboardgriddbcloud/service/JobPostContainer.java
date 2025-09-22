package com.galapea.techblog.jobboardgriddbcloud.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import com.galapea.techblog.jobboardgriddbcloud.model.JobPostType;
import com.galapea.techblog.jobboardgriddbcloud.model.WorkModel;
import com.galapea.techblog.jobboardgriddbcloud.util.DateTimeUtil;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbCloudClient;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbCloudSQLStmt;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbColumn;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbContainerDefinition;
import com.galapea.techblog.jobboardgriddbcloud.webapi.GridDbException;
import com.galapea.techblog.jobboardgriddbcloud.webapi.SQLSelectResponse;
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsRequest;
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsResponse;

@Component
public class JobPostContainer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbCloudClient gridDbCloudClient;
    private static final String TBL_NAME = "JBJobPost";

    public JobPostContainer(GridDbCloudClient gridDbCloudClient) {
        this.gridDbCloudClient = gridDbCloudClient;
    }

    public void createTable() {
        log.info("Creating table " + TBL_NAME + " in GridDB...");
        List<GridDbColumn> columns =
                List.of(
                        new GridDbColumn("id", "STRING", Set.of("TREE")),
                        new GridDbColumn("title", "STRING"),
                        new GridDbColumn("description", "STRING"),
                        new GridDbColumn("jobType", "STRING", Set.of("TREE")),
                        new GridDbColumn("maximumMonthlySalary", "DOUBLE"),
                        new GridDbColumn("datePosted", "TIMESTAMP"),
                        new GridDbColumn("companyId", "STRING", Set.of("TREE")),
                        new GridDbColumn("workModel", "STRING", Set.of("TREE")),
                        new GridDbColumn("location", "STRING"),
                        new GridDbColumn("applyUrl", "STRING"));

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

    public void insert(JobPostRecord jobPost) {
        String stmt =
                "INSERT INTO "
                        + TBL_NAME
                        + "(id, title, description, jobType, maximumMonthlySalary, datePosted, companyId, workModel, location, applyUrl) VALUES ("
                        + "'"
                        + escapeString(jobPost.id())
                        + "', "
                        + "'"
                        + StringEscapeUtils.escapeJson(jobPost.title())
                        + "', "
                        + "'"
                        + StringEscapeUtils.escapeJson(jobPost.description())
                        + "', "
                        + "'"
                        + jobPost.jobType().name()
                        + "', "
                        + jobPost.maximumMonthlySalary()
                        + ", "
                        + "'"
                        + DateTimeUtil.formatToZoneDateTimeString(jobPost.datePosted())
                        + "', "
                        + "'"
                        + escapeString(jobPost.companyId())
                        + "', "
                        + "'"
                        + jobPost.workModel().name()
                        + "', "
                        + (jobPost.location() != null
                                ? "'" + StringEscapeUtils.escapeJson(jobPost.location()) + "'"
                                : "null")
                        + ", "
                        + (jobPost.applyUrl() != null
                                ? "'" + escapeString(jobPost.applyUrl()) + "'"
                                : "null")
                        + ")";
        GridDbCloudSQLStmt insert = new GridDbCloudSQLStmt(stmt);

        post("/sql/update", List.of(insert));
    }

    public void saveRecords(List<JobPostRecord> jobPostRecords) {
        // Initialize a StringBuilder to create the JSON-like array representation
        StringBuilder sb = new StringBuilder();
        sb.append("["); // Start the outer array
        for (int i = 0; i < jobPostRecords.size(); i++) {
            JobPostRecord record = jobPostRecords.get(i);
            sb.append("["); // Start the inner array (each record)
            // Append each record field in order
            sb.append("\"").append(record.id()).append("\"");
            sb.append(", ");
            sb.append("\"").append(StringEscapeUtils.escapeJson(record.title())).append("\"");
            sb.append(", ");
            sb.append("\"").append(StringEscapeUtils.escapeJson(record.description())).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.jobType().name()).append("\"");
            sb.append(", ");
            sb.append(record.maximumMonthlySalary());
            sb.append(", ");
            sb.append("\"")
                    .append(DateTimeUtil.formatToZoneDateTimeString(record.datePosted()))
                    .append("\"");
            sb.append(", ");
            sb.append("\"").append(record.companyId()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.workModel().name()).append("\"");
            sb.append(", ");
            if (record.location() != null) {
                sb.append("\"")
                        .append(StringEscapeUtils.escapeJson(record.location()))
                        .append("\"");
            } else {
                sb.append("null");
            }
            sb.append(", ");
            if (record.applyUrl() != null) {
                sb.append("\"").append(record.applyUrl()).append("\"");
            } else {
                sb.append("null");
            }
            sb.append("]"); // End the inner array
            // Add a comma between record, except for the last one
            if (i < jobPostRecords.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]"); // End the outer array
        String result = sb.toString();
        log.info("JobPosts array: {}", result);
        this.gridDbCloudClient.registerRows(TBL_NAME, result);
    }

    public List<JobPostRecord> getAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<JobPostRecord> jobPosts = convertResponseToRecord(response.getRows());
        log.info("Fetched {} job posts from GridDB", jobPosts.size());
        return jobPosts;
    }

    public Optional<JobPostRecord> getOne(String id) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(1L).condition("id == \'" + id + "\'").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<JobPostRecord> jobPosts = convertResponseToRecord(response.getRows());
        log.info("Fetched {} job posts from GridDB", jobPosts.size());
        return jobPosts.isEmpty() ? Optional.empty() : Optional.of(jobPosts.get(0));
    }

    public List<JobPostRecord> getByCompanyId(String companyId) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(50L)
                        .condition("companyId == \'" + companyId + "\'")
                        .sort("datePosted DESC")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<JobPostRecord> jobPosts = convertResponseToRecord(response.getRows());
        log.info("Fetched {} job posts for company {} from GridDB", jobPosts.size(), companyId);
        return jobPosts;
    }

    public List<JobPostRecord> searchBySkill(String skill) {
        // @formatter:off
        String stmt =
                """
            SELECT jp.* \
            FROM JBJobPost jp \
            JOIN JBCompany c ON jp.companyId = c.id \
            JOIN JBJobPostSkill jps ON jp.id = jps.jobPostId \
            JOIN JBSkillTag st ON jps.skillTagId = st.id \
            WHERE LOWER(st.name) IN ('%s') \
            GROUP BY jp.id, c.name
        """
                        .formatted(skill.toLowerCase());
        // @formatter:on
        List<GridDbCloudSQLStmt> statementList = List.of(new GridDbCloudSQLStmt(stmt));
        SQLSelectResponse[] response = this.gridDbCloudClient.select(statementList);
        if (response == null || response.length != statementList.size()) {
            log.error(
                    "Failed to fetch jobs by skills {}. Response is null or size mismatch. Expected: {}, Actual: {}",
                    skill,
                    statementList.size(),
                    response != null ? response.length : 0);
            return List.of();
        }

        List<List<Object>> results = response[0].getResults();
        if (results.isEmpty()) {
            log.info("No result for searching skill: {}", skill);
            return List.of();
        }

        List<JobPostRecord> records = convertResponseToRecord(results);
        log.info("Fetched {} records to search skill: {}", records.size(), skill);
        return records;
    }

    private List<JobPostRecord> convertResponseToRecord(List<List<Object>> rows) {
        List<JobPostRecord> results =
                rows.stream()
                        .map(
                                row -> {
                                    try {
                                        String id = row.get(0).toString();
                                        String title = row.get(1).toString();
                                        String description = row.get(2).toString();
                                        JobPostType jobType =
                                                JobPostType.valueOf(row.get(3).toString());
                                        Double maximumMonthlySalary =
                                                Double.valueOf(row.get(4).toString());
                                        LocalDateTime datePosted =
                                                DateTimeUtil.parseToLocalDateTime(
                                                        row.get(5).toString());
                                        String companyId = row.get(6).toString();
                                        WorkModel workModel =
                                                WorkModel.valueOf(row.get(7).toString());
                                        String location =
                                                (row.get(8) != null) ? row.get(8).toString() : null;
                                        String applyUrl =
                                                (row.get(9) != null) ? row.get(9).toString() : null;

                                        return new JobPostRecord(
                                                id,
                                                title,
                                                description,
                                                jobType,
                                                maximumMonthlySalary,
                                                datePosted,
                                                companyId,
                                                workModel,
                                                location,
                                                applyUrl);
                                    } catch (Exception e) {
                                        log.error(
                                                "Error parsing job post row: {}. Error: {}",
                                                row.toString(),
                                                e.getMessage());
                                        return null;
                                    }
                                })
                        .filter(r -> r != null)
                        .collect(Collectors.toList());
        return results;
    }
}
