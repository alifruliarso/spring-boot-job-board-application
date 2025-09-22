package com.galapea.techblog.jobboardgriddbcloud.webapi;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GridDbCloudSQLStmt(@JsonProperty("stmt") String statement) {}
