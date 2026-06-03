package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.model.excel.StudentQualityScoreImportZhModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class StudentQualityScoreImportListener extends AnalysisEventListener<Map<Integer, String>> {


    private final Map<Integer,Map<Integer, String>> dataList = new HashMap<>();

    Map<Integer, String> head = new HashMap<>();

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        head = headMap;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        Integer rowIndex = context.readRowHolder().getRowIndex();
        dataList.put(rowIndex,data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 所有数据解析完成后的操作
    }

    public Map<Integer,Map<Integer, String>> getDataList() {
        return dataList;
    }
    //获取头信息
    public Map<Integer, String> getHead() {
        return head;
    }
}
