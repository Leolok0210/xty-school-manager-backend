package com.xiaotiyun.school.manager.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

@Slf4j
public class ActivityCoursesImportTemplateHeaderHandler implements SheetWriteHandler {
    private final List<String> teacherNames;

    public ActivityCoursesImportTemplateHeaderHandler(List<String> teacherNames) {
        this.teacherNames = teacherNames;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet dataSheet = workbook.createSheet("TeacherData"); // 创建隐藏工作表
        workbook.setSheetHidden(workbook.getSheetIndex(dataSheet), true); // 隐藏工作表

        // 将教师数据写入隐藏工作表
        if (ObjectUtils.isNotEmpty(teacherNames)) {
            for (int i = 0; i < teacherNames.size(); i++) {
                Row row = dataSheet.getRow(i);
                if (row == null) {
                    row = dataSheet.createRow(i);
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    cell = row.createCell(0);
                }
                cell.setCellValue(teacherNames.get(i));
            }

            // 设置数据验证引用隐藏工作表数据
            Sheet sheet = writeSheetHolder.getSheet();
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            String formula = "TeacherData!$A$1:$A$" + teacherNames.size();
            DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
            CellRangeAddressList addressList = new CellRangeAddressList(2, 102, 1, 1);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            sheet.addValidationData(dataValidation);
        }
    }
}
