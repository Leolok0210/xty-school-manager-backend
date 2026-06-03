package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentImportEnUsModel;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentImportEnUsListener extends AnalysisEventListener<StudentImportEnUsModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<StudentImportEnUsModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "Chinese Name (Required)");
        EXPECTED_HEADER.put(1, "Student ID (Required)");
        EXPECTED_HEADER.put(2, "Student ID Number (Required)");
        EXPECTED_HEADER.put(3, "Seat Number");
        EXPECTED_HEADER.put(4, "Grade Group (required，Select from dropdown)");
        EXPECTED_HEADER.put(5, "Class (required)");
        EXPECTED_HEADER.put(6, "Student Type (Select from dropdown)");
        EXPECTED_HEADER.put(7, "Foreign Name");
        EXPECTED_HEADER.put(8, "Gender（Select from dropdown）");
        EXPECTED_HEADER.put(9, "Date of Birth");
        EXPECTED_HEADER.put(10, "Place of Birth（Select from dropdown）");
        EXPECTED_HEADER.put(11, "Document Type（Select from dropdown）");
        EXPECTED_HEADER.put(12, "Document Number");
        EXPECTED_HEADER.put(13, "Document Issuing Place（Select from dropdown）");
        EXPECTED_HEADER.put(14, "Document Issuing Date");
        EXPECTED_HEADER.put(15, "Document Expiry Date");
        EXPECTED_HEADER.put(16, "Home Return Permit Number");
        EXPECTED_HEADER.put(17, "Type of Stay Permit（Select from dropdown）");
        EXPECTED_HEADER.put(18, "Stay Permit Issuing Date");
        EXPECTED_HEADER.put(19, "Stay Permit Expiry Date");
        EXPECTED_HEADER.put(20, "Nationality（Select from dropdown）");
        EXPECTED_HEADER.put(21, "Ancestral Origin");
        EXPECTED_HEADER.put(22, "Home Phone");
        EXPECTED_HEADER.put(23, "Mobile Phone");
        EXPECTED_HEADER.put(24, "Usual Address - District（Select from dropdown）");
        EXPECTED_HEADER.put(25, "Usual Address - Detailed Address");
        EXPECTED_HEADER.put(26, "Night Accommodation - District（Select from dropdown）");
        EXPECTED_HEADER.put(27, "Night Accommodation - Detailed Address");
        EXPECTED_HEADER.put(28, "Guardian's Name");
        EXPECTED_HEADER.put(29, "Guardian's Contact Phone");
        EXPECTED_HEADER.put(30, "Guardian's Mobile Phone");
        EXPECTED_HEADER.put(31, "Guardian's Occupation");
        EXPECTED_HEADER.put(32, "Guardian's Relationship to Student（Select from dropdown）");
        EXPECTED_HEADER.put(33, "Guardian's Address - District（Select from dropdown）");
        EXPECTED_HEADER.put(34, "Guardian's Address - Detailed Address");
        EXPECTED_HEADER.put(35, "Living with Guardian（Select from dropdown）");
        EXPECTED_HEADER.put(36, "Emergency Contact Name(Required)");
        EXPECTED_HEADER.put(37, "Emergency Contact Relationship to Student（Select from dropdown）");
        EXPECTED_HEADER.put(38, "Emergency Contact Phone (Required)");
        EXPECTED_HEADER.put(39, "Emergency Contact Address - District（Select from dropdown）");
        EXPECTED_HEADER.put(40, "Emergency Contact Address - Detailed Address");
        EXPECTED_HEADER.put(41, "Student WeCom Account");
        EXPECTED_HEADER.put(42, "Student Mobile Number");
        EXPECTED_HEADER.put(43, "Parent Relationship 1（Select from dropdown）");
        EXPECTED_HEADER.put(44, "Parent Mobile Number");
        EXPECTED_HEADER.put(45, "Parent Name");
        EXPECTED_HEADER.put(46, "Parent Occupation");
        EXPECTED_HEADER.put(47, "Parent Relationship 2（Select from dropdown）");
        EXPECTED_HEADER.put(48, "Parent Mobile Number");
        EXPECTED_HEADER.put(49, "Parent Name");
        EXPECTED_HEADER.put(50, "Parent Occupation");
        EXPECTED_HEADER.put(51, "Parent Relationship 3（Select from dropdown）");
        EXPECTED_HEADER.put(52, "Parent Name");
        EXPECTED_HEADER.put(53, "Parent Mobile Number");
        EXPECTED_HEADER.put(54, "Parent Relationship 4（Select from dropdown）");
        EXPECTED_HEADER.put(55, "Parent Name");
        EXPECTED_HEADER.put(56, "Parent Mobile Number");

    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (isHeaderRow) {
            // 校验表头
            for (Map.Entry<Integer, String> entry : EXPECTED_HEADER.entrySet()) {
                if (!headMap.containsKey(entry.getKey()) || !headMap.get(entry.getKey()).equals(entry.getValue())) {
                    LanguageUtil languageUtil = SpringContextUtil.getBean(LanguageUtil.class);
                    throw new RuntimeException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
                }
            }
            isHeaderRow = false;
        } else {
            isHeaderRow = true;
        }
    }

    @Override
    public void invoke(StudentImportEnUsModel studentImportModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(studentImportModel)) {
            // 非表头行，添加到数据列表中
            studentImportModel.setExcelLineNo(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(studentImportModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
