package com.maintenance.service.impl;

import com.maintenance.entity.RepairHistory;
import com.maintenance.repository.RepairHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RepairHistoryRepository repairHistoryRepository;

    public byte[] generateCostReportExcel() throws IOException {
        // 1. Lấy toàn bộ lịch sử sửa chữa từ Database
        List<RepairHistory> histories = repairHistoryRepository.findAll();

        // 2. Khởi tạo file Excel và Sheet
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Báo cáo chi phí");

            // 3. Tạo style cho hàng Tiêu đề (In đậm, nền xám)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 4. Khởi tạo hàng Tiêu đề (Dòng số 0)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Mã Sửa Chữa", "Tên Thiết Bị", "Nội Dung", "Ngày Sửa", "Chi Phí (VNĐ)", "Người Thực Hiện"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // 5. Đổ dữ liệu vào các hàng tiếp theo
            int rowIdx = 1;
            double totalCost = 0;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (RepairHistory history : histories) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(history.getRepairCode());
                row.createCell(1).setCellValue(history.getDevice() != null ? history.getDevice().getName() : "");
                row.createCell(2).setCellValue(history.getTitle());
                row.createCell(3).setCellValue(history.getRepairDate() != null ? history.getRepairDate().format(formatter) : "");

                double cost = history.getCost() != null ? history.getCost() : 0;
                row.createCell(4).setCellValue(cost);
                totalCost += cost;

                row.createCell(5).setCellValue(history.getPerformedBy() != null ? history.getPerformedBy().getFullName() : "");
            }

            // 6. Dòng Tổng cộng ở cuối cùng
            Row totalRow = sheet.createRow(rowIdx);
            Cell totalLabelCell = totalRow.createCell(3);
            totalLabelCell.setCellValue("TỔNG CỘNG:");
            totalLabelCell.setCellStyle(headerStyle);

            Cell totalValueCell = totalRow.createCell(4);
            totalValueCell.setCellValue(totalCost);
            totalValueCell.setCellStyle(headerStyle); // In đậm tổng tiền

            // 7. Căn chỉnh tự động kích thước các cột cho đẹp
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 8. Đóng gói file thành mảng byte để trả về
            workbook.write(out);
            return out.toByteArray();
        }
    }
}