package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.ConventionalPerformanceImportEnUsModel;
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
public class ConventionalPerformanceImportEnUsListener extends AnalysisEventListener<ConventionalPerformanceImportEnUsModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<ConventionalPerformanceImportEnUsModel> dataList = new ArrayList<>();

    static {
        EXPECTED_HEADER.put(0, "Student Chinese Name(required)");
        EXPECTED_HEADER.put(1, "Student ID(required)");
        EXPECTED_HEADER.put(2, "Incident Date(required)");
        EXPECTED_HEADER.put(3, "Missing Homework(required)\n" +
                "Enter 0 if no violation");
        EXPECTED_HEADER.put(4, "Missing Textbook(required)\n" +
                "Enter 0 if no violation");
        EXPECTED_HEADER.put(5, "Classroom Violation(required)\n" +
                "Enter 0 if no violation");
        EXPECTED_HEADER.put(6, "Improper Grooming(required)\n" +
                "Enter 0 if no violation");
        EXPECTED_HEADER.put(7, "Missing Return Slip(required)\n" +
                "Enter 0 if no violation");
        EXPECTED_HEADER.put(8, "Remarks (optional)\n" +
                "Remarks cannot be entered for type 0; the system can only recognize remarks for non-0 types.");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 校验表头
        for (Map.Entry<Integer, String> entry : EXPECTED_HEADER.entrySet()) {
            if (!headMap.containsKey(entry.getKey()) || !headMap.get(entry.getKey()).equals(entry.getValue())) {
                LanguageUtil languageUtil = SpringContextUtil.getBean(LanguageUtil.class);
                throw new RuntimeException(languageUtil.getMessage(LanguageConstants.FILE_FORMAT_ERROR));
            }
        }
    }

    @Override
    public void invoke(ConventionalPerformanceImportEnUsModel importZhTwModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(importZhTwModel)) {
            // 非表头行，添加到数据列表中
            importZhTwModel.setExcelLineNo(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(importZhTwModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
