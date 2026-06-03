package com.xiaotiyun.school.manager.controller.school;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.req.ClassroomPageReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomSaveReqModel;
import com.xiaotiyun.school.manager.model.req.ClassroomTypeReqModel;
import com.xiaotiyun.school.manager.model.res.ClassroomPageResModel;
import com.xiaotiyun.school.manager.model.res.ClassroomTypeResModel;
import com.xiaotiyun.school.manager.service.ClassroomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
@Api(tags = "教室管理")
public class ClassroomController extends BasicController {
    private final ClassroomService classroomService;

    @PostMapping("/type")
    @ApiOperation("新增教室类型")
    @SaCheckPermission("classroom:type:add")
    public Result<Long> addType(@Validated @RequestBody ClassroomTypeReqModel reqModel) {
        return Result.success(classroomService.addType(getSchoolId(), reqModel));
    }

    @PutMapping("/type/{id}")
    @ApiOperation("修改教室类型")
    @SaCheckPermission("classroom:type:update")
    public Result<Void> updateType(@PathVariable Long id,
                                   @Validated @RequestBody ClassroomTypeReqModel reqModel) {
        classroomService.updateType(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/type/{id}")
    @ApiOperation("删除教室类型")
    @SaCheckPermission("classroom:type:delete")
    public Result<Void> deleteType(@PathVariable Long id) {
        classroomService.deleteType(id);
        return Result.success();
    }

    @GetMapping("/type")
    @ApiOperation("教室类型列表")
    @SaCheckPermission("classroom:type:list")
    public Result<List<ClassroomTypeResModel>> typeList() {
        return Result.success(classroomService.typeList(getSchoolId()));
    }

    @PostMapping
    @ApiOperation("新增教室")
    @SaCheckPermission("classroom:add")
    public Result<Long> add(@Validated @RequestBody ClassroomSaveReqModel reqModel) {
        return Result.success(classroomService.add(getSchoolId(), reqModel));
    }

    @PutMapping("/{id}")
    @ApiOperation("修改教室")
    @SaCheckPermission("classroom:update")
    public Result<Void> update(@PathVariable Long id,
                               @Validated @RequestBody ClassroomSaveReqModel reqModel) {
        classroomService.update(id, reqModel);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除教室")
    @SaCheckPermission("classroom:delete")
    public Result<Void> delete(@PathVariable Long id) {
        classroomService.delete(id);
        return Result.success();
    }

    @GetMapping
    @ApiOperation("教室列表")
    @SaCheckPermission("classroom:page")
    public Result<PageInfo<ClassroomPageResModel>> page(@Validated ClassroomPageReqModel reqModel) {
        return Result.success(classroomService.page(getSchoolId(), reqModel));
    }
}