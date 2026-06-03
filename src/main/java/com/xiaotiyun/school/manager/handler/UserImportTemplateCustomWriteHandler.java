package com.xiaotiyun.school.manager.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 添加excel head额外内容
 */
public class UserImportTemplateCustomWriteHandler implements SheetWriteHandler {
    private String title;

    public UserImportTemplateCustomWriteHandler(String title) {
        this.title = title;
    }

    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheet(writeSheetHolder.getSheetName());
        Row row1 = sheet.getRow(0);
        if (row1 == null) {
            row1 = sheet.createRow(0);
        }
        row1.setHeight((short) 1500);
        Cell cell1 = row1.getCell(0);
        if (cell1 == null) {
            cell1 = row1.createCell(0);
        }
        cell1.setCellValue(title);

        Font font = workbook.createFont();
        font.setBold(false);
        font.setFontHeight((short) 220);
        font.setFontHeightInPoints((short) 11);
        font.setFontName("宋体");

        sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, 7));

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setFont(font);
        headStyle.setAlignment(HorizontalAlignment.LEFT);
        headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置表头背景色为白色
        headStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        CellStyle contentStyle = workbook.createCellStyle();
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                for (Cell cell : row) {
                    // 表头
                    cell.setCellStyle(headStyle);
                }
                break;
            }
        }
    }
}

