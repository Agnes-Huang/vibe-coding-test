package com.example.enrollment.dto;

import jakarta.validation.constraints.NotBlank;

public class ImportRequest {

    @NotBlank(message = "CSV内容不能为空")
    private String csvText;

    public String getCsvText() {
        return csvText;
    }

    public void setCsvText(String csvText) {
        this.csvText = csvText;
    }
}
