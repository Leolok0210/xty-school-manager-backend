package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentMedicalRecordImportEnModel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentMedicalRecordImportEnListener extends AnalysisEventListener<StudentMedicalRecordImportEnModel> {
    @Getter
    private List<StudentMedicalRecordImportEnModel> dataList = new ArrayList<>();
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "Student Chinese Name (Mandatory)");
        EXPECTED_HEADER.put(1, "Student ID (Mandatory)");
        EXPECTED_HEADER.put(2, "Visit Date (Mandatory)");
        EXPECTED_HEADER.put(3, "Visit Time (Mandatory)");
        EXPECTED_HEADER.put(4, "Treatment (Mandatory)");
        EXPECTED_HEADER.put(5, "Remarks (Mandatory)");
        EXPECTED_HEADER.put(6, "Temperature");
        EXPECTED_HEADER.put(7, "Fever");
        EXPECTED_HEADER.put(8, "Cough");
        EXPECTED_HEADER.put(9, "Runny Nose");
        EXPECTED_HEADER.put(10, "Sore Throat");
        EXPECTED_HEADER.put(11, "Dizziness");
        EXPECTED_HEADER.put(12, "Headache");
        EXPECTED_HEADER.put(13, "Nosebleed");
        EXPECTED_HEADER.put(14, "Nausea");
        EXPECTED_HEADER.put(15, "Vomiting Frequency");
        EXPECTED_HEADER.put(16, "Abdominal Pain");
        EXPECTED_HEADER.put(17, "Diarrhea Frequency");
        EXPECTED_HEADER.put(18, "Other Symptoms");
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
    public void invoke(StudentMedicalRecordImportEnModel data, AnalysisContext context) {
        if (!ObjectUtils.areAllFieldsEmpty(data)) {
            // 非表头行，添加到数据列表中
            data.setExcelLineNo(context.readRowHolder().getRowIndex() + 1);
            dataList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 解析完成后的操作
    }

}