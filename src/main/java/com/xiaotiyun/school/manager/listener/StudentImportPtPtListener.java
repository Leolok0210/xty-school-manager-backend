package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentImportPtPtModel;
import com.xiaotiyun.school.manager.basic.util.ObjectUtils;
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
public class StudentImportPtPtListener extends AnalysisEventListener<StudentImportPtPtModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<StudentImportPtPtModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "Nome em Chinês (Obrigatório)");
        EXPECTED_HEADER.put(1, "Número de Estudante (Obrigatório)");
        EXPECTED_HEADER.put(2, "Número do Cartão de Estudante (Obrigatório)");
        EXPECTED_HEADER.put(3, "Número do Lugar");
        EXPECTED_HEADER.put(4, "Grupo de Nível(Obrigatório，Selecionar da lista)");
        EXPECTED_HEADER.put(5, "Turma (obrigatório)");
        EXPECTED_HEADER.put(6, "Tipo de Estudante (Selecionar da lista)");
        EXPECTED_HEADER.put(7, "Nome em Língua Estrangeira");
        EXPECTED_HEADER.put(8, "Género（Selecionar da lista)");
        EXPECTED_HEADER.put(9, "Data de Nascimento");
        EXPECTED_HEADER.put(10, "Local de Nascimento（Selecionar da lista)");
        EXPECTED_HEADER.put(11, "Tipo de Documento（Selecionar da lista)");
        EXPECTED_HEADER.put(12, "Número do Documento");
        EXPECTED_HEADER.put(13, "Local de Emissão do Documento（Selecionar da lista)");
        EXPECTED_HEADER.put(14, "Data de Emissão do Documento");
        EXPECTED_HEADER.put(15, "Data de Validade do Documento");
        EXPECTED_HEADER.put(16, "Número do Permisso de Regresso");
        EXPECTED_HEADER.put(17, "Tipo de Autorização de Estadia（Selecionar da lista)");
        EXPECTED_HEADER.put(18, "Data de Emissão da Autorização");
        EXPECTED_HEADER.put(19, "Data de Validade da Autorização");
        EXPECTED_HEADER.put(20, "Nacionalidade（Selecionar da lista)");
        EXPECTED_HEADER.put(21, "Origem Ancestral");
        EXPECTED_HEADER.put(22, "Telefone de Casa");
        EXPECTED_HEADER.put(23, "Telemóvel");
        EXPECTED_HEADER.put(24, "Morada Habitual - Distrito（Selecionar da lista)");
        EXPECTED_HEADER.put(25, "Morada Habitual - Endereço Completo");
        EXPECTED_HEADER.put(26, "Morada Noturna - Distrito（Selecionar da lista)");
        EXPECTED_HEADER.put(27, "Morada Noturna - Endereço Completo");
        EXPECTED_HEADER.put(28, "Nome do Tutor");
        EXPECTED_HEADER.put(29, "Telefone de Contacto do Tutor");
        EXPECTED_HEADER.put(30, "Telemóvel do Tutor");
        EXPECTED_HEADER.put(31, "Profissão do Tutor");
        EXPECTED_HEADER.put(32, "Relação do Tutor com o Estudante（Selecionar da lista)");
        EXPECTED_HEADER.put(33, "Morada do Tutor - Distrito（Selecionar da lista)");
        EXPECTED_HEADER.put(34, "Morada do Tutor - Endereço Completo");
        EXPECTED_HEADER.put(35, "Vive com o Tuto（Selecionar da lista)");
        EXPECTED_HEADER.put(36, "Nome do Contacto de Emergência(Obrigatório)");
        EXPECTED_HEADER.put(37, "Relação com o Estudante（Selecionar da lista)");
        EXPECTED_HEADER.put(38, "Telefone de Emergência (Obrigatório)");
        EXPECTED_HEADER.put(39, "Morada do Contacto de Emergência - Distrito（Selecionar da lista)");
        EXPECTED_HEADER.put(40, "Morada do Contacto de Emergência - Endereço Completo");
        EXPECTED_HEADER.put(41, "Conta WeCom do Estudante");
        EXPECTED_HEADER.put(42, "Número de Telefone do Estudante");
        EXPECTED_HEADER.put(43, "Grau de Parentesco 1（Selecionar da lista)");
        EXPECTED_HEADER.put(44, "Número de Telefone do Responsável");
        EXPECTED_HEADER.put(45, "Nome do Responsável");
        EXPECTED_HEADER.put(46, "Profissão do Responsável");
        EXPECTED_HEADER.put(47, "Grau de Parentesco 2（Selecionar da lista)");
        EXPECTED_HEADER.put(48, "Número de Telefone do Responsável");
        EXPECTED_HEADER.put(49, "Nome do Responsável");
        EXPECTED_HEADER.put(50, "Profissão do Responsável");
        EXPECTED_HEADER.put(51, "Grau de Parentesco 3（Selecionar da lista)");
        EXPECTED_HEADER.put(52, "Nome do Responsável");
        EXPECTED_HEADER.put(53, "Número de Telefone do Responsável");
        EXPECTED_HEADER.put(54, "Grau de Parentesco 4（Selecionar da lista)");
        EXPECTED_HEADER.put(55, "Nome do Responsável");
        EXPECTED_HEADER.put(56, "Número de Telefone do Responsável");
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
    public void invoke(StudentImportPtPtModel studentImportModel, AnalysisContext analysisContext) {
        if (!ObjectUtils.areAllFieldsEmpty(studentImportModel)) {
            // 非表头行，添加到数据列表中
            studentImportModel.setExcelLineNo(analysisContext.readRowHolder().getRowIndex() + 1);
            dataList.add(studentImportModel);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
