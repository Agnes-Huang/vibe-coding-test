package com.example.enrollment.dto;

import com.example.enrollment.entity.EnrollRecord;

import java.util.List;
import java.util.Map;

public class EnrollResponse {
    private List<EnrollRecord> records;
    private Map<String, List<EnrollRecord>> groupedByType;
    private String message;
    private long elapsedMs;

    public EnrollResponse(List<EnrollRecord> records, Map<String, List<EnrollRecord>> groupedByType,
                          String message, long elapsedMs) {
        this.records = records;
        this.groupedByType = groupedByType;
        this.message = message;
        this.elapsedMs = elapsedMs;
    }

    public List<EnrollRecord> getRecords() {
        return records;
    }

    public void setRecords(List<EnrollRecord> records) {
        this.records = records;
    }

    public Map<String, List<EnrollRecord>> getGroupedByType() {
        return groupedByType;
    }

    public void setGroupedByType(Map<String, List<EnrollRecord>> groupedByType) {
        this.groupedByType = groupedByType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(long elapsedMs) {
        this.elapsedMs = elapsedMs;
    }
}
