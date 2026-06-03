package com.xiaotiyun.school.manager.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.helper.DepartmentCheckHelper;
import com.xiaotiyun.school.manager.model.req.SystemSettingUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.SystemSettingResModel;
import com.xiaotiyun.school.manager.service.SystemSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/systemSetting")
@Api(tags = "系统设置接口")
public class SystemSettingController extends BasicController {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private DepartmentCheckHelper departmentCheckHelper;

    @ApiOperation(value = "获取学校配置", notes = "返回学校的所有系统配置项")
    @GetMapping("/get/{schoolId}")
    public Result<SystemSettingResModel> getSettings(
            @ApiParam(value = "学校ID", required = true)
            @PathVariable Long schoolId) {
        return Result.success(systemSettingService.getSchoolSettings(schoolId));
    }

    @ApiOperation(value = "更新学校配置", notes = "更新学校的系统配置项,只更新传入的配置项,未传入的配置项保持不变")
    @SaCheckPermission("system:setting:edit")
    @PostMapping("/update")
    public Result<Void> updateSettings(
            @ApiParam(value = "配置更新请求", required = true)
            @Valid @RequestBody SystemSettingUpdateReqModel reqModel) {
        // 新增的校验逻辑
        if (reqModel.getSettings().containsKey("departments")) {
            Map<Integer,Integer> departmentMap = new HashMap<>();
            String departmentsJson = reqModel.getSettings().get("departments");
            if(!StringUtils.isEmpty(departmentsJson)) {
                List<JSONObject> jsonObjects = JSONObject.parseArray(departmentsJson, JSONObject.class);
                for (JSONObject department : jsonObjects) {
                    String departmentType = department.getString("name");
                    if (DepartmentEnum.KINDERGARTEN.getDesc().equals(departmentType)) {
                        String years = department.getString("years");
                        if (years != null && !years.isEmpty() && Integer.parseInt(years) > 3) {
                            return Result.failed(ResultCode.DATA_YIKE_MAX_COUNT);
                        }
                    } else {
                        String years = department.getString("years");
                        if (years != null && !years.isEmpty() && Integer.parseInt(years) > 6) {
                            return Result.failed(ResultCode.DATA_XIAOXUE_MAX_COUNT);
                        }
                    }
                    //判断数据
                    DepartmentEnum departmentEnum = DepartmentEnum.getByDesc(departmentType);
                    if(departmentEnum == null) {
                        return Result.failed();
                    }
                    departmentMap.put(departmentEnum.getCode(), departmentEnum.getCode());
                }
            }
            //判断是否有数据
            SystemSettingResModel schoolSettings = systemSettingService.getSchoolSettings(reqModel.getSchoolId());
            if(schoolSettings != null){
                Map<String, String> settings = schoolSettings.getSettings();
                if(!CollectionUtils.isEmpty(settings))
                {
                    String oldDepartmentsJson = settings.get("departments");
                    if(!StringUtils.isEmpty(oldDepartmentsJson)) {
                        List<JSONObject> jsonObjects = JSONObject.parseArray(oldDepartmentsJson, JSONObject.class);
                        for (JSONObject department : jsonObjects) {
                            String departmentType = department.getString("name");
                            //判断数据
                            DepartmentEnum departmentEnum = DepartmentEnum.getByDesc(departmentType);
                            Integer id = departmentMap.get(departmentEnum.getCode());
                            if(id == null) {
                                boolean valid = departmentCheckHelper.canUnselectDepartment(departmentEnum.getCode(), reqModel.getSchoolId());
                                if(!valid){
                                    return Result.failed(ResultCode.DEPARTMENT_HAS_DATA);
                                }
                            }
                        }
                    }
                }
            }
        }



        systemSettingService.updateSettings(reqModel);
        return Result.success();
    }
}