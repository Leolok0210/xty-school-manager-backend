package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.ActivityStudentReportImportEnModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动匹配导入监听器（英文）
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class ActivityStudentReportImportEnListener extends AnalysisEventListener<ActivityStudentReportImportEnModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<ActivityStudentReportImportEnModel> dataList = new ArrayList<>();

    static {
        EXPECTED_HEADER.put(0, "Student（required）");
        EXPECTED_HEADER.put(1, "Student code（required）");
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
    public void invoke(ActivityStudentReportImportEnModel model, AnalysisContext context) {
        if (!ObjectUtils.areAllFieldsEmpty(model)) {
            // 非表头行，添加到数据列表中
            model.setRowIndex(context.readRowHolder().getRowIndex() + 1);
            dataList.add(model);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Activity matching import data parsing completed, total {} records", dataList.size());
    }
} 