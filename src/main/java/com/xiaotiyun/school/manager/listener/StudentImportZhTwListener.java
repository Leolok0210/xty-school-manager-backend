package com.xiaotiyun.school.manager.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.basic.util.SpringContextUtil;
import com.xiaotiyun.school.manager.model.excel.StudentImportZhTwModel;
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
public class StudentImportZhTwListener extends AnalysisEventListener<StudentImportZhTwModel> {
    // 存储期望的表头
    private static final Map<Integer, String> EXPECTED_HEADER = new HashMap<>();
    private List<StudentImportZhTwModel> dataList = new ArrayList<>();
    // 由于表头在第二行，我们需要一个标志来跳过第一行
    private boolean isHeaderRow = false;

    static {
        EXPECTED_HEADER.put(0, "中文姓名（必填）");
        EXPECTED_HEADER.put(1, "學生編號（必填）");
        EXPECTED_HEADER.put(2, "學生證編號（必填）");
        EXPECTED_HEADER.put(3, "班內號");
        EXPECTED_HEADER.put(4, "級组（必填，下拉选择）");
        EXPECTED_HEADER.put(5, "班級名稱（必填）");
        EXPECTED_HEADER.put(6, "學生類型（下拉选择）");
        EXPECTED_HEADER.put(7, "外文姓名");
        EXPECTED_HEADER.put(8, "性別（下拉选择）");
        EXPECTED_HEADER.put(9, "出生日期");
        EXPECTED_HEADER.put(10, "出生地點（下拉选择）");
        EXPECTED_HEADER.put(11, "證件類別（下拉选择）");
        EXPECTED_HEADER.put(12, "證件編號");
        EXPECTED_HEADER.put(13, "證件發出地點（下拉选择）");
        EXPECTED_HEADER.put(14, "證件發出日期");
        EXPECTED_HEADER.put(15, "證件有效日期");
        EXPECTED_HEADER.put(16, "回鄉證編號");
        EXPECTED_HEADER.put(17, "逗留許可類型（下拉选择）");
        EXPECTED_HEADER.put(18, "逗留許可發出日期");
        EXPECTED_HEADER.put(19, "逗留許可有效日期");
        EXPECTED_HEADER.put(20, "國籍（下拉选择）");
        EXPECTED_HEADER.put(21, "籍貫");
        EXPECTED_HEADER.put(22, "住址電話");
        EXPECTED_HEADER.put(23, "手提電話");
        EXPECTED_HEADER.put(24, "常用住址-地區（下拉选择）");
        EXPECTED_HEADER.put(25, "常用住址-詳細地址");
        EXPECTED_HEADER.put(26, "夜間留宿住址-地區（下拉选择）");
        EXPECTED_HEADER.put(27, "夜間留宿住址-詳細地址");
        EXPECTED_HEADER.put(28, "監護人姓名");
        EXPECTED_HEADER.put(29, "監護人聯絡電話");
        EXPECTED_HEADER.put(30, "監護人流動電話");
        EXPECTED_HEADER.put(31, "監護人職業");
        EXPECTED_HEADER.put(32, "監護人和學生關係（下拉选择）");
        EXPECTED_HEADER.put(33, "監護人住址-地區（下拉选择）");
        EXPECTED_HEADER.put(34, "監護人住址-詳細地址");
        EXPECTED_HEADER.put(35, "與監護人同住（下拉选择）");
        EXPECTED_HEADER.put(36, "緊急聯絡人姓名（必填）");
        EXPECTED_HEADER.put(37, "緊急聯絡人與學生關係（下拉选择）");
        EXPECTED_HEADER.put(38, "緊急聯絡人聯絡電話（必填）");
        EXPECTED_HEADER.put(39, "緊急聯絡人住址-地區（下拉选择）");
        EXPECTED_HEADER.put(40, "緊急聯絡人住址-詳細地址");
        EXPECTED_HEADER.put(41, "學生企微賬號");
        EXPECTED_HEADER.put(42, "學生手機號");
        EXPECTED_HEADER.put(43, "家長關係一（下拉选择）");
        EXPECTED_HEADER.put(44, "家長手機號");
        EXPECTED_HEADER.put(45, "家長姓名");
        EXPECTED_HEADER.put(46, "家長職業");
        EXPECTED_HEADER.put(47, "家長關係二（下拉选择）");
        EXPECTED_HEADER.put(48, "家長手機號");
        EXPECTED_HEADER.put(49, "家長姓名");
        EXPECTED_HEADER.put(50, "家長職業");
        EXPECTED_HEADER.put(51, "家長關係三（下拉选择）");
        EXPECTED_HEADER.put(52, "家長姓名");
        EXPECTED_HEADER.put(53, "家長手機號");
        EXPECTED_HEADER.put(54, "家長關係四（下拉选择）");
        EXPECTED_HEADER.put(55, "家長姓名");
        EXPECTED_HEADER.put(56, "家長手機號");

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
    public void invoke(StudentImportZhTwModel studentImportModel, AnalysisContext analysisContext) {
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