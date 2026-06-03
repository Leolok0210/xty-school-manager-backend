package com.xiaotiyun.school.manager.handler;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTypeEntity;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyRuleImportResModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.List;

@Slf4j
public class UsualRuleImportTemplateHeaderHandler implements SheetWriteHandler {
    private final List<StudentUsuallyRuleImportResModel> data;
    private final List<StudentUsuallyTypeEntity> typeList;

    public UsualRuleImportTemplateHeaderHandler(List<StudentUsuallyRuleImportResModel> data, List<StudentUsuallyTypeEntity> typeList) {
        this.data = data;
        this.typeList = typeList;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 插入表格数据，从第三行开始插入数据
        Sheet sheet = writeSheetHolder.getSheet();

        // 创建带边框的单元格样式
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        CellStyle contentStyle = workbook.createCellStyle();
        // 设置水平居中对齐
        contentStyle.setAlignment(HorizontalAlignment.CENTER);
        // 设置垂直居中对齐
        contentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置四个边框为细线
        contentStyle.setBorderTop(BorderStyle.THIN);
        contentStyle.setBorderBottom(BorderStyle.THIN);
        contentStyle.setBorderLeft(BorderStyle.THIN);
        contentStyle.setBorderRight(BorderStyle.THIN);

        if (ObjectUtils.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.getRow(i + 2);
                if (row == null) {
                    row = sheet.createRow(i + 2); // 只在行不存在时创建
                }
                StudentUsuallyRuleImportResModel model = data.get(i);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue(model.getGradeGroupName());
                cell1.setCellStyle(contentStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(model.getSubjectName());
                cell2.setCellStyle(contentStyle);
                Cell cell3 = row.createCell(2);
                cell3.setCellValue(model.getTypeName());
                cell3.setCellStyle(contentStyle);
                Cell cell4 = row.createCell(3);
                cell4.setCellValue(model.getWeight() == null? 0: model.getWeight());
                cell4.setCellStyle(contentStyle);
            }
        }

        // 将平时成绩类型数据写入隐藏工作表
        if (ObjectUtils.isNotEmpty(typeList)) {
            Sheet dataSheet = workbook.createSheet("typeData"); // 创建隐藏工作表
            workbook.setSheetHidden(workbook.getSheetIndex(dataSheet), true); // 隐藏工作表
            for (int i = 0; i < typeList.size(); i++) {
                Row row = dataSheet.getRow(i);
                if (row == null) {
                    row = dataSheet.createRow(i);
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    cell = row.createCell(0);
                }
                cell.setCellValue(typeList.get(i).getTypeName());
            }

            // 设置数据验证引用隐藏工作表数据
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            String formula = "typeData!$A$1:$A$" + typeList.size();
            DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(formula);
            CellRangeAddressList addressList = new CellRangeAddressList(2, 10000, 2, 2);
            DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setShowErrorBox(true);
            sheet.addValidationData(dataValidation);
        }
    }
}
