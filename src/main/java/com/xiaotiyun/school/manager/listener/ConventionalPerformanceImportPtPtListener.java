package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.ConventionalPerformanceImportPtPtModel;
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
public class ConventionalPerformanceImportPtPtListener extends AnalysisEventListener<ConventionalPerformanceImportPtPtModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<ConventionalPerformanceImportPtPtModel> dataList = new ArrayList<>();

    static {
        EXPECTED_HEADER.put(0, "Nome em Chinês do Aluno (obrigatório)");
        EXPECTED_HEADER.put(1, "Número do Aluno  (obrigatório)");
        EXPECTED_HEADER.put(2, "Data do Incidente (obrigatório)");
        EXPECTED_HEADER.put(3, "Tarefas em Falta (obrigatório)\n" +
                "Introduza 0 se não houver infração");
        EXPECTED_HEADER.put(4, "Livro em Falta (obrigatório)\n" +
                "Introduza 0 se não houver infração");
        EXPECTED_HEADER.put(5, "Infração em Sala de Aula (obrigatório)\n" +
                "Introduza 0 se não houver infração");
        EXPECTED_HEADER.put(6, "Aparência Impropria (obrigatório)\n" +
                "Introduza 0 se não houver infração");
        EXPECTED_HEADER.put(7, "Comprovativo em Falta (obrigatório)\n" +
                "Introduza 0 se não houver infração");
        EXPECTED_HEADER.put(8, "Observação (opcional)\n" +
                "Não é permitido inserir observação para o tipo igual a 0, o sistema só reconhece observações de tipos diferentes de 0");
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
    public void invoke(ConventionalPerformanceImportPtPtModel importZhTwModel, AnalysisContext analysisContext) {
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
