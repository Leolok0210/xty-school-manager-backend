-- AI 報表生成系統 - 數據表結構

CREATE TABLE IF NOT EXISTS `ai_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主鍵ID',
  `user_id` BIGINT NOT NULL COMMENT '用戶ID',
  `school_id` BIGINT DEFAULT NULL COMMENT '學校ID',
  `report_type` VARCHAR(50) DEFAULT NULL COMMENT '報表類型（student_list, grade_report, attendance_report）',
  `format` VARCHAR(20) DEFAULT NULL COMMENT '檔案格式（xlsx, csv, pdf）',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '檔案路徑',
  `report_name` VARCHAR(200) DEFAULT NULL COMMENT '報表名稱',
  `content_summary` TEXT COMMENT '報表摘要',
  `query_params` JSON DEFAULT NULL COMMENT '查詢參數',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '創建時間',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新時間',
  `deleted` TINYINT DEFAULT 0 COMMENT '刪除標記（0=未刪除，1=已刪除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_school` (`user_id`, `school_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI報表記錄表';