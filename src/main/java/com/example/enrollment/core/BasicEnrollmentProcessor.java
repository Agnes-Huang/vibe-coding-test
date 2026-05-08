package com.example.enrollment.core;

import com.example.enrollment.entity.EnrollRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A部分：学生选课基础处理工具（可独立运行）
 */
public final class BasicEnrollmentProcessor {

    private BasicEnrollmentProcessor() {
    }

    public static List<EnrollRecord> processEnrollments(List<EnrollRecord> input) {
        Set<String> dedupKeys = new LinkedHashSet<>();
        List<EnrollRecord> result = new ArrayList<>();
        for (EnrollRecord record : input) {
            String key = record.getStudentId() + "#" + record.getCourseId();
            if (dedupKeys.add(key)) {
                result.add(record);
            }
        }

        result.sort(Comparator.comparing(EnrollRecord::getStudentId).thenComparing(EnrollRecord::getCourseId));
        return result;
    }

    public static void printEnrollments(List<EnrollRecord> records) {
        records.forEach(record -> System.out.println(record.toString()));
    }

    public static void main(String[] args) {
        List<EnrollRecord> input = List.of(
                new EnrollRecord("S000002", "C000003", "计算机网络"),
                new EnrollRecord("S000001", "C000001", "Java程序设计"),
                new EnrollRecord("S000001", "C000001", "Java程序设计（重复，不应保留）"),
                new EnrollRecord("S000001", "C000002", "数据库原理")
        );
        List<EnrollRecord> processed = processEnrollments(input);
        printEnrollments(processed);
    }
}
