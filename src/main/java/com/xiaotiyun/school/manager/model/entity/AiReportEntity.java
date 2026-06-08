package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_report")
public class AiReportEntity {
    /**
     * 主鍵ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用戶ID
     */
    private Long userId;

    /**
     * 學校ID
     */
    private Long schoolId;

    /**
     * 報表類型（student_list, grade_report, attendance_report, etc）
     */
    private String reportType;

    /**
     * 檔案格式（xlsx, csv, pdf）
     */
    private String format;

    /**
     * 檔案路徑
     */
    private String filePath;

    /**
     * 報表名稱
     */
    private String reportName;

    /**
     * 報表摘要
     */
    private String contentSummary;

    /**
     * 查詢參數（JSON格式）
     */
    private String queryParams;

    /**
     * 創建時間
     */
    private LocalDateTime createTime;

    /**
     * 更新時間
     */
    private LocalDateTime updateTime;

    /**
     * 刪除標記（0=未刪除，1=已刪除）
     */
    private Integer deleted;
}