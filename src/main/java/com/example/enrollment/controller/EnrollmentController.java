package com.example.enrollment.controller;

import com.example.enrollment.dto.EnrollResponse;
import com.example.enrollment.dto.ImportRequest;
import com.example.enrollment.entity.EnrollRecord;
import com.example.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public ResponseEntity<EnrollResponse> getAll() {
        long start = System.nanoTime();
        List<EnrollRecord> records = enrollmentService.getAllProcessed();
        Map<String, List<EnrollRecord>> grouped = enrollmentService.groupByCourseType(records);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        return ResponseEntity.ok(new EnrollResponse(records, grouped, "查询成功", elapsedMs));
    }

    @PostMapping("/import")
    public ResponseEntity<EnrollResponse> importCsv(@Valid @RequestBody ImportRequest request) {
        long start = System.nanoTime();
        List<EnrollRecord> records = enrollmentService.importFromCsvText(request.getCsvText());
        enrollmentService.printEnrollments(records);
        Map<String, List<EnrollRecord>> grouped = enrollmentService.groupByCourseType(records);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        return ResponseEntity.ok(new EnrollResponse(records, grouped, "导入并处理成功", elapsedMs));
    }

    @GetMapping("/search")
    public ResponseEntity<EnrollResponse> search(@RequestParam(required = false) String keyword) {
        long start = System.nanoTime();
        List<EnrollRecord> records = enrollmentService.search(keyword);
        Map<String, List<EnrollRecord>> grouped = enrollmentService.groupByCourseType(records);
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        String message = records.isEmpty() ? "无匹配选课记录" : "检索成功";
        return ResponseEntity.ok(new EnrollResponse(records, grouped, message, elapsedMs));
    }
}
