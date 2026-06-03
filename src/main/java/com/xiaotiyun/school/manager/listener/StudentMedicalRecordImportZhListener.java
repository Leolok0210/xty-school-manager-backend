package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentMedicalRecordImportZhModel;
import lombok.Getter;

import java.util.*;

public class StudentMedicalRecordImportZhListener extends AnalysisEventListener<StudentMedicalRecordImportZhModel> {
    @Getter
    private List<StudentMedicalRecordImportZhModel> dataList = new ArrayList<>();

    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "學生中文姓名（必填）");
        EXPECTED_HEADER.put(1, "學生編號（必填）");
        EXPECTED_HEADER.put(2, "就診日期（必填）");
        EXPECTED_HEADER.put(3, "就診時間（必填）");
        EXPECTED_HEADER.put(4, "處理（必填）");
        EXPECTED_HEADER.put(5, "備註（必填）");
        EXPECTED_HEADER.put(6, "體溫");
        EXPECTED_HEADER.put(7, "發熱");
        EXPECTED_HEADER.put(8, "咳嗽");
        EXPECTED_HEADER.put(9, "流涕");
        EXPECTED_HEADER.put(10, "咽痛");
        EXPECTED_HEADER.put(11, "頭暈");
        EXPECTED_HEADER.put(12, "頭痛");
        EXPECTED_HEADER.put(13, "流鼻血");
        EXPECTED_HEADER.put(14, "噁心");
        EXPECTED_HEADER.put(15, "嘔吐次數");
        EXPECTED_HEADER.put(16, "腹痛");
        EXPECTED_HEADER.put(17, "腹瀉次數");
        EXPECTED_HEADER.put(18, "其他症狀");
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
    public void invoke(StudentMedicalRecordImportZhModel data, AnalysisContext context) {
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