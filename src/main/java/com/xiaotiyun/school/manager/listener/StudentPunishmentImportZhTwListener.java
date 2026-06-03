package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentPunishmentImportZhTwModel;
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
public class StudentPunishmentImportZhTwListener extends AnalysisEventListener<StudentPunishmentImportZhTwModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<StudentPunishmentImportZhTwModel> dataList = new ArrayList<>();

    static {
        EXPECTED_HEADER.put(0, "學生中文姓名（必填）");
        EXPECTED_HEADER.put(1, "學生編號（必填）");
        EXPECTED_HEADER.put(2, "會議通過日期（必填）");
        EXPECTED_HEADER.put(3, "原因（必填）");
        EXPECTED_HEADER.put(4, "類型（必填）\n" +
                "可輸入：大過、小過、缺點");
        EXPECTED_HEADER.put(5, "次數（必填）");
        EXPECTED_HEADER.put(6, "備註");
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
    public void invoke(StudentPunishmentImportZhTwModel importZhTwModel, AnalysisContext analysisContext) {
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
