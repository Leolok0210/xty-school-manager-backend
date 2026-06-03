package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.SysClassZhImportModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysClassImportListener extends AnalysisEventListener<SysClassZhImportModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<SysClassZhImportModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;
    static {
        EXPECTED_HEADER.put(0, "級組（必选，下拉选择）");
        EXPECTED_HEADER.put(1, "班級序號（必填）");
        EXPECTED_HEADER.put(2, "班級名稱");
//        EXPECTED_HEADER.put(3, "是否專業班（必填）");
        EXPECTED_HEADER.put(3, "文理科（下拉选择）");
        EXPECTED_HEADER.put(4, "專業名稱");
        EXPECTED_HEADER.put(5, "班主任用户编号（必填）");
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        // 校验表头
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
    public void invoke(SysClassZhImportModel sysClassImportModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(sysClassImportModel)) {
            // 非表头行，添加到数据列表中
            sysClassImportModel.setRowIndex(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(sysClassImportModel);
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}