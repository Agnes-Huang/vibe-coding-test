package com.example.enrollment.service;

import com.example.enrollment.entity.EnrollRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class EnrollmentServiceTest {

    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService();
        enrollmentService.initSampleData();
    }

    @Test
    void shouldDeduplicateByStudentIdAndCourseIdAndSort() {
        List<EnrollRecord> input = List.of(
                new EnrollRecord("S000002", "C000003", "计算机网络", "公共课"),
                new EnrollRecord("S000001", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000001", "C000001", "Java程序设计（重复）", "专业课"),
                new EnrollRecord("S000001", "C000002", "数据库原理", "专业课")
        );
        List<EnrollRecord> result = enrollmentService.processEnrollments(input);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("S000001", result.get(0).getStudentId());
        Assertions.assertEquals("C000001", result.get(0).getCourseId());
        Assertions.assertEquals("S000001", result.get(1).getStudentId());
        Assertions.assertEquals("C000002", result.get(1).getCourseId());
        Assertions.assertEquals("S000002", result.get(2).getStudentId());
    }

    @Test
    void shouldSearchWithinOneSecondForMoreThanOneThousandRecords() {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < 1200; i++) {
            String studentId = String.format("S%06d", (i % 700) + 1);
            String courseId = String.format("C%06d", (i % 200) + 1);
            String courseName = i % 2 == 0 ? "Java程序设计" : "大学英语";
            String courseType = i % 2 == 0 ? "专业课" : "公共课";
            lines.add(studentId + "," + courseId + "," + courseName + "," + courseType);
        }

        enrollmentService.importFromCsvText(String.join("\n", lines));
        long start = System.nanoTime();
        List<EnrollRecord> result = enrollmentService.search("Java");
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertTrue(elapsedMs <= 1000, "检索耗时超过1秒，实际：" + elapsedMs + "ms");
    }

    @Test
    void shouldSupportLabeledChineseInputFormat() {
        String input = "学生ID：S001244，课程ID：C000161，课程名称：数学";
        enrollmentService.importFromCsvText(input);

        List<EnrollRecord> result = enrollmentService.search("S001244");
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("C000161", result.get(0).getCourseId());
    }

    @Test
    void shouldRejectCourseIdConflictWithDifferentCourseName() {
        String input = String.join("\n",
                "S001245,C000888,高等数学,公共课",
                "S001246,C000888,线性代数,公共课"
        );
        EnrollmentService.ImportResult importResult = enrollmentService.importFromCsvText(input);
        List<EnrollRecord> result = enrollmentService.search("C000888");

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("高等数学", result.get(0).getCourseName());
        Assertions.assertTrue(importResult.getMessage().contains("1 行因课程ID与既有课程信息不一致被跳过"));
    }
}
