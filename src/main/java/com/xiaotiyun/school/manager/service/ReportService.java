package com.xiaotiyun.school.manager.service;

import com.xiaotiyun.school.manager.model.entity.AiReportEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {
    /**
     * 生成報表並返回檔案路徑
     */
    AiReportEntity generateReport(Long userId, Long schoolId, String reportType, String format, String queryParams);

    /**
     * 獲取報表下載位址
     */
    String getDownloadPath(Long reportId);

    /**
     * 上傳報表並解析存入數據庫
     */
    AiReportEntity uploadReport(MultipartFile file, Long userId, Long schoolId);

    /**
     * 獲取用戶的報表列表
     */
    List<AiReportEntity> getUserReports(Long userId, Long schoolId);

    /**
     * 刪除報表
     */
    void deleteReport(Long reportId);
}