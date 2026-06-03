package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.ConventionalPerformanceImportZhTwModel;
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
public class ConventionalPerformanceImportZhTwListener extends AnalysisEventListener<ConventionalPerformanceImportZhTwModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<ConventionalPerformanceImportZhTwModel> dataList = new ArrayList<>();
    // 表頭行標註
    boolean isHeaderRow = true;

    static {
        EXPECTED_HEADER.put(0, "學生中文姓名（必填）");
        EXPECTED_HEADER.put(1, "學生編號（必填）");
        EXPECTED_HEADER.put(2, "事件日期（必填）");
        EXPECTED_HEADER.put(3, "欠作業（必填）\n" +
                "無該違規請輸入0");
        EXPECTED_HEADER.put(4, "欠課本（必填）\n" +
                "無該違規請輸入0");
        EXPECTED_HEADER.put(5, "上课违规（必填）\n" +
                "無該違規請輸入0");
        EXPECTED_HEADER.put(6, "儀表不符（必填）\n" +
                "無該違規請輸入0");
        EXPECTED_HEADER.put(7, "欠回條（必填）\n" +
                "無該違規請輸入0");
        EXPECTED_HEADER.put(8, "備註（選填）\n" +
                "表現為 0 的類型不可填寫備註，系統僅能辨識非 0 類型的備註");
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
    public void invoke(ConventionalPerformanceImportZhTwModel importZhTwModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(importZhTwModel)) {
            // 表头行，跳过
            if (isHeaderRow) {
                isHeaderRow = false;
                return;
            }
            // 非表头行，添加到数据列表中
            importZhTwModel.setExcelLineNo(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(importZhTwModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
