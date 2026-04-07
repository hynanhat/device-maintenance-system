package com.maintenance.controller;

import com.maintenance.service.impl.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/costs/excel")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Tải file Excel báo cáo tổng hợp chi phí sửa chữa")
    public ResponseEntity<byte[]> downloadCostReport() {
        try {
            // Lấy cục dữ liệu Excel từ Service
            byte[] excelContent = reportService.generateCostReportExcel();

            // Cấu hình Header để trình duyệt tự động nhận dạng đây là file cần tải xuống
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "Bao_Cao_Chi_Phi_Sua_Chua.xlsx");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}