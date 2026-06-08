package com.xiaotiyun.school.manager.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.mapper.AiReportMapper;
import com.xiaotiyun.school.manager.model.entity.AiReportEntity;
import com.xiaotiyun.school.manager.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@Api(tags = "報表管理")
public class ReportController extends BasicController {

    @Resource
    private ReportService reportService;

    @Resource
    private AiReportMapper aiReportMapper;

    @GetMapping("/list")
    @ApiOperation("獲取我的報表列表")
    public Result<List<AiReportEntity>> getMyReports() {
        Long userId = getUserId();
        Long schoolId = getSchoolId();
        List<AiReportEntity> reports = reportService.getUserReports(userId, schoolId);
        return Result.success(reports);
    }

    @SaIgnore
    @GetMapping("/download/{reportId}")
    @ApiOperation("下載報表")
    public void downloadReport(@PathVariable Long reportId, HttpServletResponse response) {
        AiReportEntity report = aiReportMapper.selectById(reportId);
        if (report == null || report.getFilePath() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(report.getFilePath());
        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + report.getReportName());

            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/upload")
    @ApiOperation("上傳報表")
    public Result<AiReportEntity> uploadReport(@RequestParam("file") MultipartFile file) {
        Long userId = getUserId();
        Long schoolId = getSchoolId();
        AiReportEntity report = reportService.uploadReport(file, userId, schoolId);
        return Result.success(report);
    }

    @DeleteMapping("/{reportId}")
    @ApiOperation("刪除報表")
    public Result<Void> deleteReport(@PathVariable Long reportId) {
        reportService.deleteReport(reportId);
        return Result.success();
    }
}