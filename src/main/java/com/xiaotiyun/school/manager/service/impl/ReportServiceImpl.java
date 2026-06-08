package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.dao.StudentMapper;
import com.xiaotiyun.school.manager.mapper.AiReportMapper;
import com.xiaotiyun.school.manager.model.entity.AiReportEntity;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private AiReportMapper aiReportMapper;

    @Resource
    private StudentMapper studentMapper;

    private static final String REPORT_PATH = "/mnt/vdb/file/report/";

    @Override
    public AiReportEntity generateReport(Long userId, Long schoolId, String reportType, String format, String queryParams) {
        // 確保目錄存在
        File dir = new File(REPORT_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 根據報表類型生成內容
        String fileName = reportType + "_" + System.currentTimeMillis() + "." + format;
        String filePath = REPORT_PATH + fileName;

        try {
            if ("xlsx".equals(format)) {
                generateExcelReport(filePath, reportType, queryParams);
            } else if ("csv".equals(format)) {
                generateCsvReport(filePath, reportType, queryParams);
            }

            // 保存報表記錄
            AiReportEntity report = new AiReportEntity();
            report.setUserId(userId);
            report.setSchoolId(schoolId);
            report.setReportType(reportType);
            report.setFormat(format);
            report.setFilePath(filePath);
            report.setReportName(getReportDisplayName(reportType));
            report.setQueryParams(queryParams);
            report.setCreateTime(LocalDateTime.now());
            report.setUpdateTime(LocalDateTime.now());
            report.setDeleted(0);
            aiReportMapper.insert(report);

            return report;
        } catch (Exception e) {
            log.error("生成報表失敗", e);
            throw new RuntimeException("生成報表失敗: " + e.getMessage());
        }
    }

    private void generateExcelReport(String filePath, String reportType, String queryParams) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(reportType);

        // 創建表頭樣式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 根據報表類型填充數據
        if ("student_list".equals(reportType)) {
            createStudentListSheet(sheet, queryParams, headerStyle);
        } else {
            // 通用空白表格
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("報表類型: " + reportType);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private void createStudentListSheet(Sheet sheet, String queryParams, CellStyle headerStyle) {
        // 表頭
        Row headerRow = sheet.createRow(0);
        String[] headers = {"學號", "姓名", "性別", "班級"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 解析查詢參數
        JSONObject params = JSON.parseObject(queryParams);
        Long schoolId = params != null ? params.getLong("schoolId") : null;
        String className = params != null ? params.getString("className") : null;

        // 查詢學生數據
        List<StudentEntity> students = null;
        if (schoolId != null) {
            students = studentMapper.selectList(
                new LambdaQueryWrapper<StudentEntity>()
                    .eq(StudentEntity::getSchoolId, schoolId)
                    .last("LIMIT 100")
            );
        }

        // 填充數據
        if (students != null && !students.isEmpty()) {
            int rowNum = 1;
            for (StudentEntity student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getStudentNo() != null ? student.getStudentNo() : "");
                row.createCell(1).setCellValue(student.getChineseName() != null ? student.getChineseName() : "");
                row.createCell(2).setCellValue(student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "");
                row.createCell(3).setCellValue(className != null ? className : "");
            }
        }
    }

    private void generateCsvReport(String filePath, String reportType, String queryParams) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("報表類型: ").append(reportType).append("\n");
        sb.append("生成時間: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        sb.append("查詢參數: ").append(queryParams).append("\n");

        // 根據類型添加數據
        if ("student_list".equals(reportType)) {
            sb.append("\n學號,姓名,性別,班級\n");
            JSONObject params = JSON.parseObject(queryParams);
            Long schoolId = params != null ? params.getLong("schoolId") : null;
            if (schoolId != null) {
                List<StudentEntity> students = studentMapper.selectList(
                    new LambdaQueryWrapper<StudentEntity>()
                        .eq(StudentEntity::getSchoolId, schoolId)
                        .last("LIMIT 100")
                );
                if (students != null) {
                    for (StudentEntity student : students) {
                        sb.append(student.getStudentNo() != null ? student.getStudentNo() : "").append(",");
                        sb.append(student.getChineseName() != null ? student.getChineseName() : "").append(",");
                        sb.append(student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "").append(",");
                        sb.append("\n");
                    }
                }
            }
        }

        FileUtil.writeString(sb.toString(), filePath, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String getReportDisplayName(String reportType) {
        switch (reportType) {
            case "student_list":
                return "學生名單";
            case "grade_report":
                return "成績報表";
            case "attendance_report":
                return "考勤報表";
            default:
                return "報表";
        }
    }

    @Override
    public String getDownloadPath(Long reportId) {
        AiReportEntity report = aiReportMapper.selectById(reportId);
        if (report != null && report.getFilePath() != null) {
            return "/api/report/download/" + reportId;
        }
        return null;
    }

    @Override
    public AiReportEntity uploadReport(MultipartFile file, Long userId, Long schoolId) {
        // 解析上傳的檔案並存儲
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File dest = new File(REPORT_PATH + fileName);

        try {
            file.transferTo(dest);

            AiReportEntity report = new AiReportEntity();
            report.setUserId(userId);
            report.setSchoolId(schoolId);
            report.setReportName(file.getOriginalFilename());
            report.setFilePath(dest.getAbsolutePath());
            report.setFormat(getFileExtension(file.getOriginalFilename()));
            report.setCreateTime(LocalDateTime.now());
            report.setUpdateTime(LocalDateTime.now());
            report.setDeleted(0);
            aiReportMapper.insert(report);

            return report;
        } catch (IOException e) {
            log.error("上傳報表失敗", e);
            throw new RuntimeException("上傳報表失敗: " + e.getMessage());
        }
    }

    @Override
    public List<AiReportEntity> getUserReports(Long userId, Long schoolId) {
        return aiReportMapper.selectList(
            new LambdaQueryWrapper<AiReportEntity>()
                .eq(AiReportEntity::getUserId, userId)
                .eq(AiReportEntity::getSchoolId, schoolId)
                .eq(AiReportEntity::getDeleted, 0)
                .orderByDesc(AiReportEntity::getCreateTime)
        );
    }

    @Override
    public void deleteReport(Long reportId) {
        AiReportEntity report = aiReportMapper.selectById(reportId);
        if (report != null) {
            // 刪除文件
            if (report.getFilePath() != null) {
                File file = new File(report.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
            // 軟刪除記錄
            report.setDeleted(1);
            report.setUpdateTime(LocalDateTime.now());
            aiReportMapper.updateById(report);
        }
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "";
    }
}