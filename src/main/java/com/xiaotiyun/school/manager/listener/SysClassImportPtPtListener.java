package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.SysClassPtImportModel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class SysClassImportPtPtListener extends AnalysisEventListener<SysClassPtImportModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<SysClassPtImportModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;
    static {
        EXPECTED_HEADER.put(0, "Grupo de Nível (obrigatório，Selecionar da lista)");
        EXPECTED_HEADER.put(1, "Número da Turma (obrigatório)");
        EXPECTED_HEADER.put(2, "Nome da Turma");
//        EXPECTED_HEADER.put(3, "É Turma Especializada (obrigatório)");
        EXPECTED_HEADER.put(3, "Área（Selecionar da lista)");
        EXPECTED_HEADER.put(4, "Nome do Curso");
        EXPECTED_HEADER.put(5, "Número de Utilizador do Professor Titular (obrigatório)");
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
    public void invoke(SysClassPtImportModel sysClassImportModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(sysClassImportModel)) {
            // 非表头行，添加到数据列表中
            sysClassImportModel.setRowIndex(analysisContext.readRowHolder().getRowIndex());
            dataList.add(sysClassImportModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    // 获取读取到的数据列表
    public List<SysClassPtImportModel> getDataList() {
        return dataList;
    }
}