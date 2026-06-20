package com.xiaotiyun.school.manager.controller.device;

import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.entity.CampusPhotoEntity;
import com.xiaotiyun.school.manager.service.CampusPhotoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Api(tags = "校园风采管理-后台")
@RequestMapping("/api/manage/campus-photos")
@RequiredArgsConstructor
public class CampusPhotoManageController {
    private final CampusPhotoService campusPhotoService;

    @PostMapping("/upload")
    @ApiOperation(value = "上传校园风采照片")
    public Result<CampusPhotoEntity> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("schoolId") Long schoolId) {
        try {
            CampusPhotoEntity entity = campusPhotoService.upload(file, schoolId);
            return Result.success(entity);
        } catch (Exception e) {
            return Result.failed(ResultCode.FAILED);
        }
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询校园风采照片列表")
    public Result<List<CampusPhotoEntity>> list(@RequestParam("schoolId") Long schoolId) {
        return Result.success(campusPhotoService.listPhotos(schoolId));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除校园风采照片")
    public Result<String> delete(@PathVariable Long id) {
        boolean ok = campusPhotoService.deletePhoto(id);
        return ok ? Result.success("ok") : Result.failed(ResultCode.NOT_FOUND);
    }
}
