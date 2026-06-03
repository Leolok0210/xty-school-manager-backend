package com.xiaotiyun.school.manager.controller.transcript;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsQueryReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptDetailsSaveReqModel;
import com.xiaotiyun.school.manager.model.req.TranScriptGenerateReqModel;
import com.xiaotiyun.school.manager.model.res.KindergartenTranscriptResModel;
import com.xiaotiyun.school.manager.model.res.TranScriptDetailsResModel;
import com.xiaotiyun.school.manager.model.res.TranScriptGenerateResModel;
import com.xiaotiyun.school.manager.service.TranScriptDetailsService;
import com.xiaotiyun.school.manager.service.SysClassService;
import com.xiaotiyun.school.manager.service.TranScriptGenerateService;
import com.xiaotiyun.school.manager.service.impl.TranScriptGenerateServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transcript/details")
@RequiredArgsConstructor
@Api(tags = "成绩单详情")
public class TranScriptDetailsController extends BasicController {

	private final TranScriptGenerateService tranScriptGenerateService;

	private final SysClassService sysClassService;

	private final TranScriptDetailsService tranScriptDetailsService;


	@ApiOperation("查询班级成绩单详情列表；权限符：transcript:details:query")
	@SaCheckPermission("transcript:details:query")
	@PostMapping("/list")
	public Result<List<TranScriptDetailsResModel>> list(@Validated @RequestBody TranScriptDetailsQueryReqModel reqModel) {
		return Result.success(tranScriptDetailsService.list(reqModel, getSchoolId()));
	}

	@ApiOperation("保存或更新成绩单详情（python脚本调用）")
	@PostMapping("/saveOrUpdate")
	@SaIgnore
	public Result<Boolean> saveOrUpdate(@Validated @RequestBody TranScriptDetailsSaveReqModel reqModel) {
		return Result.success(tranScriptDetailsService.saveOrUpdate(reqModel, getSchoolId()));
	}


	@ApiOperation("计算班级成绩（python脚本调用）")
	@PostMapping("/calculate")
	@SaIgnore
	public Result classList(@Validated @RequestBody TranScriptGenerateReqModel reqModel) {
        SysClass sysClass = sysClassService.getSysClassById(reqModel.getClassId());
        if(sysClass==null)
        {
            return Result.failed(ResultCode.FAILED);
        }
        if(sysClass.getDepartment().equals(DepartmentEnum.KINDERGARTEN.getCode()))
        {
            return Result.success(tranScriptGenerateService.generateKindergarten(reqModel));
        }else {
            return Result.success(tranScriptGenerateService.generate(reqModel));
        }
	}

	@ApiOperation("删除成绩单详情（预留接口）")
	@DeleteMapping("/delete/{id}")
	@SaIgnore
	public Result<Boolean> delete(@PathVariable Long id) {
		return Result.success(tranScriptDetailsService.delete(id, getSchoolId()));
	}

	@ApiOperation("获取成绩单详情，权限符：权限符：transcript:details:query（预留接口）")
	@SaCheckPermission("transcript:details:query")
	@GetMapping("/info/{id}")
	public Result<TranScriptDetailsResModel> info(@PathVariable Long id) {
		return Result.success(tranScriptDetailsService.info(id, getSchoolId()));
	}
}