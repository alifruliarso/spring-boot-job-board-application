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
public class CompanyContainer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GridDbCloudClient gridDbCloudClient;
    private static final String TBL_NAME = "JBCompany";

    public CompanyContainer(GridDbCloudClient gridDbCloudClient) {
        this.gridDbCloudClient = gridDbCloudClient;
    }

    public void createTable() {
        log.info("Creating table " + TBL_NAME + " in GridDB...");
        List<GridDbColumn> columns =
                List.of(
                        new GridDbColumn("id", "STRING", Set.of("TREE")),
                        new GridDbColumn("name", "STRING"),
                        new GridDbColumn("websiteUrl", "STRING"),
                        new GridDbColumn("description", "STRING"));

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

    public void insert(CompanyRecord cat) {
        String stmt =
                "INSERT INTO "
                        + TBL_NAME
                        + "(id, name, websiteUrl, description) VALUES ('"
                        + cat.id()
                        + "', '"
                        + cat.name()
                        + "', '"
                        + cat.websiteUrl()
                        + "', '"
                        + cat.description()
                        + "')";
        GridDbCloudSQLStmt insert = new GridDbCloudSQLStmt(stmt);

        post("/sql/update", List.of(insert));
    }

    public void saveRecords(List<CompanyRecord> cRecords) {
        // Initialize a StringBuilder to create the JSON-like array representation
        StringBuilder sb = new StringBuilder();
        sb.append("["); // Start the outer array
        for (int i = 0; i < cRecords.size(); i++) {
            CompanyRecord record = cRecords.get(i);
            sb.append("["); // Start the inner array (each record)
            // Append each record field in order
            sb.append("\"").append(record.id()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.name()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.websiteUrl()).append("\"");
            sb.append(", ");
            sb.append("\"").append(record.description()).append("\"");
            sb.append("]"); // End the inner array
            // Add a comma between record, except for the last one
            if (i < cRecords.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]"); // End the outer array
        String result = sb.toString();
        log.info("Companys array: {}", result);
        this.gridDbCloudClient.registerRows(TBL_NAME, result);
    }

    public List<CompanyRecord> getAll() {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(50L).sort("id ASC").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return List.of();
        }
        List<CompanyRecord> companys = convertResponseToRecord(response);
        log.info("Fetched {} companys from GridDB", companys.size());
        return companys;
    }

    public Optional<CompanyRecord> getOne(String id) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder().limit(1L).condition("id == \'" + id + "\'").build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<CompanyRecord> companys = convertResponseToRecord(response);
        log.info("Fetched {} companys from GridDB", companys.size());
        return companys.isEmpty() ? Optional.empty() : Optional.of(companys.get(0));
    }

    public Optional<CompanyRecord> getOneByName(String name) {
        AcquireRowsRequest requestBody =
                AcquireRowsRequest.builder()
                        .limit(1L)
                        .condition("name == \'" + name + "\'")
                        .build();
        AcquireRowsResponse response = this.gridDbCloudClient.acquireRows(TBL_NAME, requestBody);
        if (response == null || response.getRows() == null) {
            log.error("Failed to acquire rows from GridDB");
            return Optional.empty();
        }
        List<CompanyRecord> companys = convertResponseToRecord(response);
        log.info("Fetched {} companys from GridDB", companys.size());
        return companys.isEmpty() ? Optional.empty() : Optional.of(companys.get(0));
    }

    private List<CompanyRecord> convertResponseToRecord(AcquireRowsResponse response) {
        List<CompanyRecord> results =
                response.getRows().stream()
                        .map(
                                row -> {
                                    try {
                                        var cat =
                                                new CompanyRecord(
                                                        row.get(0).toString(),
                                                        row.get(1).toString(),
                                                        row.get(2).toString(),
                                                        row.get(3).toString());
                                        return cat;
                                    } catch (Exception e) {
                                        log.error(
                                                "Error parsing company row: {}. Error: {}",
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
