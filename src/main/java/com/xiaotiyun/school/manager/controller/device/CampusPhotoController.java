package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.config.FileConfig;
import com.xiaotiyun.school.manager.model.entity.CampusPhotoEntity;
import com.xiaotiyun.school.manager.service.CampusPhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@Api(tags = "校园风采-设备端")
@RequestMapping("/api/campus-photos")
@RequiredArgsConstructor
public class CampusPhotoController {
    private final CampusPhotoService campusPhotoService;
    private final FileConfig fileConfig;

    @GetMapping("/list")
    @ApiOperation(value = "照片列表（设备端拉取用）")
    public Result<List<CampusPhotoEntity>> list(@RequestParam("schoolId") Long schoolId) {
        return Result.success(campusPhotoService.listPhotos(schoolId));
    }

    @GetMapping("/file/{schoolId}/{fileName}")
    @ApiOperation(value = "查看照片文件")
    public ResponseEntity<Resource> getFile(
            @PathVariable Long schoolId,
            @PathVariable String fileName) {
        File baseDir = new File(fileConfig.getFileRootPath(), schoolId + "/campus");
        File file = new File(baseDir, fileName);
        // 防止路径遍历：确保解析后的文件仍在该校的 campus 目录内
        try {
            String basePath = baseDir.getCanonicalPath();
            String filePath = file.getCanonicalPath();
            if (!filePath.startsWith(basePath + File.separator)) {
                return ResponseEntity.badRequest().build();
            }
        } catch (java.io.IOException e) {
            return ResponseEntity.badRequest().build();
        }
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String lower = fileName.toLowerCase();
        String contentType;
        if (lower.endsWith(".png")) contentType = "image/png";
        else if (lower.endsWith(".webp")) contentType = "image/webp";
        else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (lower.endsWith(".mp4")) contentType = "video/mp4";
        else contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
