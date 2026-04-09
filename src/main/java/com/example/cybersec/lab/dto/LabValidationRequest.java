package com.example.cybersec.lab.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO xử lý yêu cầu xác thực kết quả thực hành Lab.
 * Chứa payload do người dùng gửi lên và loại lab đang kiểm tra.
 */
public class LabValidationRequest {
    @NotBlank(message = "Lab type is required")
    private String labType;

    @NotBlank(message = "Input is required")
    private String input;

    public String getLabType() { return labType; }
    public void setLabType(String labType) { this.labType = labType; }
    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
}
