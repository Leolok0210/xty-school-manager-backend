package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.LeisureActivitiesScoreImportEnUsModel;
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
public class LeisureActivitiesScoreImportEnUsListener extends AnalysisEventListener<LeisureActivitiesScoreImportEnUsModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<LeisureActivitiesScoreImportEnUsModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "course（required）");
        EXPECTED_HEADER.put(1, "student（required）");
        EXPECTED_HEADER.put(2, "Student code（required）");
        EXPECTED_HEADER.put(3, "Attendance frequency（required）");
        EXPECTED_HEADER.put(4, "Classroom performance score（required）");
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
    public void invoke(LeisureActivitiesScoreImportEnUsModel importZhTwModel, AnalysisContext analysisContext) {
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
