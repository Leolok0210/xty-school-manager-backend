package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.SubjectImportEnUsModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SubjectImportEnUsListener extends AnalysisEventListener<SubjectImportEnUsModel> {
    private final List<SubjectImportEnUsModel> dataList = new ArrayList<>();

    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();

    static {
        EXPECTED_HEADER.put(0, "Subject Code (required)");
        EXPECTED_HEADER.put(1, "Subject Name (required)");
        EXPECTED_HEADER.put(2, "English Name");
        EXPECTED_HEADER.put(3, "Unit \n" +
                "(Format: number, 1-100)");
        EXPECTED_HEADER.put(4, "Applicable Department (required)\n" +
                "Supported inputs: Kindergarten, Primary School, Secondary School \n" +
                "For multiple values: separate with comma \",\"");
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
    public void invoke(SubjectImportEnUsModel data, AnalysisContext context) {
        if (!ObjectUtils.areAllFieldsEmpty(data)) {
            Integer rowIndex = context.readRowHolder().getRowIndex();
            data.setRowIndex(rowIndex);
            dataList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据解析完成后的操作
    }

    public List<SubjectImportEnUsModel> getDataList() {
        return dataList;
    }
}