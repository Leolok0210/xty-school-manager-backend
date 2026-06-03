package com.xiaotiyun.school.manager.service.ai.skill.impl;

import com.xiaotiyun.school.manager.basic.enums.FileTypeEnum;
import com.xiaotiyun.school.manager.handler.ExportFileHandler;
import com.xiaotiyun.school.manager.service.ai.skill.AiContext;
import com.xiaotiyun.school.manager.service.ai.skill.AiSkill;
import com.xiaotiyun.school.manager.service.ai.skill.SkillResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class ExportTableSkill implements AiSkill {

    @Resource
    private ExportFileHandler exportFileHandler;

    @Override
    public String getName() {
        return "export_table";
    }

    @Override
    public String getDescription() {
        return "將表格數據導出為 Excel 文件並返回下載連結";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("data", createMap("type", "array", "description", "要導出的數據陣列，每個元素是一個 Map"));
        properties.put("headers", createMap("type", "array", "description", "表格標題陣列"));
        properties.put("fileName", createMap("type", "string", "description", "文件名稱（不含副檔名）"));
        properties.put("sheetName", createMap("type", "string", "description", "工作表名稱"));
        params.put("properties", properties);
        return params;
    }

    @Override
    public boolean isAvailableForRole(String role) {
        return true;
    }

    @Override
    public SkillResult execute(Map<String, Object> params, AiContext context) {
        try {
            String fileName = params.get("fileName") != null ? params.get("fileName").toString() : "export";
            String sheetName = params.get("sheetName") != null ? params.get("sheetName").toString() : "數據";

            // 處理 headers
            List<List<String>> headers = new ArrayList<>();
            Object headersObj = params.get("headers");
            if (headersObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> headersList = (List<Object>) headersObj;
                List<String> headerRow = new ArrayList<>();
                for (Object h : headersList) {
                    headerRow.add(h != null ? h.toString() : "");
                }
                headers.add(headerRow);
            }

            // 處理 data
            List<List<String>> data = new ArrayList<>();
            Object dataObj = params.get("data");
            if (dataObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> dataList = (List<Object>) dataObj;
                for (Object rowObj : dataList) {
                    if (rowObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> row = (Map<String, Object>) rowObj;
                        List<String> rowData = new ArrayList<>();
                        // 如果有 headers，按順序提取數據
                        if (headersObj instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Object> headersList = (List<Object>) headersObj;
                            for (Object h : headersList) {
                                Object val = row.get(h != null ? h.toString() : "");
                                rowData.add(val != null ? val.toString() : "");
                            }
                        } else {
                            // 否則按 Map 的順序
                            for (Object val : row.values()) {
                                rowData.add(val != null ? val.toString() : "");
                            }
                        }
                        data.add(rowData);
                    }
                }
            }

            // 確保文件名有副檔名
            if (!fileName.endsWith(".xlsx")) {
                fileName = fileName + ".xlsx";
            }

            // 導出 Excel
            String url = exportFileHandler.doExportExcelCommon(data, fileName, headers, FileTypeEnum.EXPORT, context.getSchoolId());

            String message = "已成功導出文件，下載連結：\n" + url + "\n\n請點擊上方連結下載 Excel 文件。";

            List<Map<String, Object>> cards = new ArrayList<>();
            Map<String, Object> card = new HashMap<>();
            card.put("下載連結", url);
            card.put("文件名", fileName);
            cards.add(card);

            return SkillResult.ok(message, null, cards);
        } catch (Exception e) {
            log.error("export_table error", e);
            return SkillResult.fail("導出失敗：" + e.getMessage());
        }
    }

    private Map<String, Object> createMap(Object... kvs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < kvs.length; i += 2) {
            map.put(kvs[i].toString(), kvs[i + 1]);
        }
        return map;
    }
}