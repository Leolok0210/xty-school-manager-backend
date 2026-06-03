package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.TeacherAttendanceImportEnUsModel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TeacherAttendanceImportEnUsListener extends AnalysisEventListener<TeacherAttendanceImportEnUsModel> {
    private final List<TeacherAttendanceImportEnUsModel> dataList = new ArrayList<>();

    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();

    static {
        EXPECTED_HEADER.put(0, "Teacher User ID (required)");
        EXPECTED_HEADER.put(1, "Teacher's Name (required)");
        EXPECTED_HEADER.put(2, "Card Number");
        EXPECTED_HEADER.put(3, "Date (required)");
        EXPECTED_HEADER.put(4, "Time (required)");
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
    public void invoke(TeacherAttendanceImportEnUsModel importModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(importModel)) {
            // 非表头行，添加到数据列表中
            importModel.setExcelLineNo(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(importModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 解析完成后的操作
    }
} 