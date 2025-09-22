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
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsRequest;
import com.galapea.techblog.jobboardgriddbcloud.webapi.acquisition.AcquireRowsResponse;

@Component
public class SkillTagContainer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbCloudClient gridDbCloudClient;
    private static final String TBL_NAME = "JBSkillTag";

    public SkillTagContainer(GridDbCloudClient gridDbCloudClient) {
        this.gridDbCloudClient = gridDbCloudClient;
    }

    public void createTable() {
        log.info("Creating table " + TBL_NAME + " in GridDB...");
        List<GridDbColumn> columns =
                List.of(
                        new GridDbColumn("id", "STRING", Set.of("TREE")),
                        new GridDbColumn("name", "STRING", Set.of("TREE")));

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

    public void insert(SkillTagRecord skillTag) {
        String stmt =
                "INSERT INTO "
                        + TBL_NAME
                        + "(id, name) VALUES ("
                        + "'"
                        + escapeString(skillTag.id())
                        + "', "
                        + "'"
                        + escapeString(skillTag.name())
                        + "'"
                        + ")";
        GridDbCloudSQLStmt insert = new GridDbCloudSQLStmt(stmt);

        post("/sql/update", List.of(insert));
    }

    public void saveRecords(List<SkillTagRecord> skillTagRecords) {
        // Initialize a StringBuilder to create the JSON-like array representation
        StringBuilder sb = new StringBuilder();
        sb.append("["); // Start the outer array
        for (int i = 0; i < skillTagRecords.size(); i++) {
            SkillTagRecord record = skillTagRecords.get(i);
            sb.append("["); // Start the inner array (each record)
            // Append each record field in order
            sb.append("\"").append(record.id()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.name()).append("\"");
            sb.append("]"); // End the inner array
            // Add a comma between record, except for the last one
            if (i < skillTagRecords.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]"); // End the outer array
        String result = sb.toString();
        log.info("SkillTags array: {}", result);
        this.gridDbCloudClient.registerRows(TBL_NAME, result);
    }

    public List<SkillTagRecord> getAll() {
        return getAll(50L);
    }

    public List<SkillTagRecord> getAll(Long limit) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(limit).sort("name ASC").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<SkillTagRecord> skillTags = convertResponseToRecord(response);
        log.info("Fetched {} skill tags from GridDB", skillTags.size());
        return skillTags;
    }

    public Optional<SkillTagRecord> getOne(String id) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(1L).condition("id == \'" + id + "\'").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<SkillTagRecord> skillTags = convertResponseToRecord(response);
        log.info("Fetched {} skill tags from GridDB", skillTags.size());
        return skillTags.isEmpty() ? Optional.empty() : Optional.of(skillTags.get(0));
    }

    public Optional<SkillTagRecord> getOneByName(String name) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(1L)
                        .condition("name == \'" + escapeString(name) + "\'")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<SkillTagRecord> skillTags = convertResponseToRecord(response);
        log.info("Fetched {} skill tags from GridDB", skillTags.size());
        return skillTags.isEmpty() ? Optional.empty() : Optional.of(skillTags.get(0));
    }

    public List<SkillTagRecord> searchByName(String namePattern) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(50L)
                        .condition("name LIKE '%" + escapeString(namePattern) + "%'")
                        .sort("name ASC")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<SkillTagRecord> skillTags = convertResponseToRecord(response);
        log.info(
                "Found {} skill tags matching pattern '{}' from GridDB",
                skillTags.size(),
                namePattern);
        return skillTags;
    }

    private List<SkillTagRecord> convertResponseToRecord(AcquireRowsResponse response) {
        List<SkillTagRecord> results =
                response.getRows().stream()
                        .map(
                                row -> {
                                    try {
                                        String id = row.get(0).toString();
                                        String name = row.get(1).toString();

                                        return new SkillTagRecord(id, name);
                                    } catch (Exception e) {
                                        log.error(
                                                "Error parsing skill tag row: {}. Error: {}",
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
