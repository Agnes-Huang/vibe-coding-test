package com.example.enrollment.service;

import com.example.enrollment.entity.EnrollRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private static final List<String> TYPE_ORDER = List.of("公共课", "专业课", "选修课", "其他");
    private final List<EnrollRecord> storage = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initSampleData() {
        List<EnrollRecord> samples = List.of(
                new EnrollRecord("S000001", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000002", "C000002", "大学英语", "公共课"),
                new EnrollRecord("S000003", "C000003", "摄影基础", "选修课"),
                new EnrollRecord("S000001", "C000001", "Java程序设计（重复）", "专业课")
        );
        storage.clear();
        storage.addAll(processEnrollments(samples));
    }

    /**
     * 第一题核心逻辑：去重 + 排序
     */
    public List<EnrollRecord> processEnrollments(List<EnrollRecord> input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> uniqueKeys = new LinkedHashSet<>();
        List<EnrollRecord> deduped = new ArrayList<>();
        for (EnrollRecord record : input) {
            if (record == null) {
                continue;
            }
            String normalizedType = normalizeType(record.getCourseType(), record.getCourseName());
            record.setCourseType(normalizedType);
            String key = record.dedupKey();
            if (uniqueKeys.add(key)) {
                deduped.add(record);
            }
        }

        deduped.sort(Comparator.comparing(EnrollRecord::getStudentId).thenComparing(EnrollRecord::getCourseId));
        return deduped;
    }

    /**
     * 第一题输出逻辑：逐行打印
     */
    public void printEnrollments(List<EnrollRecord> records) {
        records.forEach(record -> System.out.println(record.toString()));
    }

    public List<EnrollRecord> importFromCsvText(String csvText) {
        String[] lines = csvText.split("\\r?\\n");
        List<EnrollRecord> imported = new ArrayList<>();

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String normalizedLine = normalizeInputLine(line);
            String[] parts = normalizedLine.split("\\s*,\\s*");
            if (parts.length < 3) {
                continue;
            }
            String studentId = parts[0].trim();
            String courseId = parts[1].trim();
            String courseName = parts[2].trim();
            String courseType = parts.length >= 4 ? parts[3].trim() : "";

            if (studentId.isEmpty() || courseId.isEmpty() || courseName.isEmpty()) {
                continue;
            }
            imported.add(new EnrollRecord(studentId, courseId, courseName, courseType));
        }

        List<EnrollRecord> merged = new ArrayList<>(storage);
        merged.addAll(imported);
        List<EnrollRecord> processed = processEnrollments(merged);
        storage.clear();
        storage.addAll(processed);
        return processed;
    }

    /**
     * 兼容两种输入：
     * 1) 标准CSV：S000001,C000001,Java程序设计,专业课
     * 2) 标签格式：学生ID：S000001，课程ID：C000001，课程名称：Java程序设计，课程类型：专业课
     */
    private String normalizeInputLine(String line) {
        return line.trim()
                .replace("，", ",")
                .replace("；", ";")
                .replace("学生ID：", "")
                .replace("学生ID:", "")
                .replace("课程ID：", "")
                .replace("课程ID:", "")
                .replace("课程名称：", "")
                .replace("课程名称:", "")
                .replace("课程类型：", "")
                .replace("课程类型:", "");
    }

    public List<EnrollRecord> getAllProcessed() {
        return processEnrollments(new ArrayList<>(storage));
    }

    public List<EnrollRecord> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllProcessed();
        }
        String normalized = keyword.trim().toLowerCase(Locale.ROOT);

        return getAllProcessed().stream()
                .filter(record -> containsIgnoreCase(record.getStudentId(), normalized)
                        || containsIgnoreCase(record.getCourseId(), normalized)
                        || containsIgnoreCase(record.getCourseName(), normalized)
                        || containsIgnoreCase(record.getCourseType(), normalized))
                .collect(Collectors.toList());
    }

    public Map<String, List<EnrollRecord>> groupByCourseType(List<EnrollRecord> records) {
        Map<String, List<EnrollRecord>> grouped = new LinkedHashMap<>();
        TYPE_ORDER.forEach(type -> grouped.put(type, new ArrayList<>()));

        for (EnrollRecord record : records) {
            String type = normalizeType(record.getCourseType(), record.getCourseName());
            grouped.computeIfAbsent(type, k -> new ArrayList<>()).add(record);
        }
        return grouped;
    }

    private String normalizeType(String inputType, String courseName) {
        if (inputType != null && !inputType.isBlank()) {
            String trimmed = inputType.trim();
            if (TYPE_ORDER.contains(trimmed)) {
                return trimmed;
            }
        }
        return inferTypeFromCourseName(courseName);
    }

    private String inferTypeFromCourseName(String courseName) {
        String name = courseName == null ? "" : courseName.trim();
        if (name.contains("程序设计") || name.contains("网络") || name.contains("数据库")) {
            return "专业课";
        }
        if (name.contains("英语") || name.contains("数学") || name.contains("思政")) {
            return "公共课";
        }
        return "选修课";
    }

    private boolean containsIgnoreCase(String value, String keywordLowerCase) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keywordLowerCase);
    }
}
