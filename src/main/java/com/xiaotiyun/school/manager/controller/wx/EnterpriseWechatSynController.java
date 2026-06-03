package com.xiaotiyun.school.manager.controller.wx;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatSynRecordResModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatSynResModel;
import com.xiaotiyun.school.manager.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 企业微信关联同步表控制器
 */
@RestController
@RequestMapping("/api/enterprise/wechat/syn")
@Api(tags = "企业微信关联同步管理")
public class EnterpriseWechatSynController extends BasicController {

    @Resource
    private EnterpriseWechatSynService enterpriseWechatSynService;

    @Resource
    private EnterpriseWechatSynRecordService enterpriseWechatSynRecordService;

    @Resource
    private SysClassService sysClassService;

    @Resource
    private GradeGroupService gradeGroupService;


    @Resource
    private StudentService studentService;


    /**
     * 分页查询企业微信关联同步任务
     * @param pageNum
     * @param pageSize
     * @param type 联类型 1-级组 2-班级 3-学生 4-家长
     */
    @GetMapping("/list")
    @ApiOperation("分页查询企业微信关联同步任务")
    public Result<PageInfo<EnterpriseWechatSynResModel>> list(@RequestParam Integer pageNum,
                                                              @RequestParam Integer pageSize,
                                                              @RequestParam Integer type) {
        return Result.success(enterpriseWechatSynService.list(pageNum, pageSize, type));
    }

    /**
     * 企业微信关联同步任务详情
     */
    @GetMapping("/detail")
    @ApiOperation("企业微信关联同步任务详情")
    public Result<List<EnterpriseWechatSynRecordResModel>> detail(@RequestParam Long taskId) {

        EnterpriseWechatSynEntity synEntity = enterpriseWechatSynService.getById(taskId);

        LambdaQueryWrapper<EnterpriseWechatSynRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatSynRecordEntity::getTaskId, taskId);
        List<EnterpriseWechatSynRecordEntity> list = enterpriseWechatSynRecordService.list(queryWrapper);
        if (list != null && !list.isEmpty())
        {
            List<Long> relIds = list.stream().map(EnterpriseWechatSynRecordEntity::getRelId).collect(Collectors.toList());
            Map<Long, String> relNameMap = new HashMap<>();
            switch (synEntity.getType())
            {
                case 1:
                    //级组
                    relNameMap = gradeGroupService.listByIds(relIds).stream().collect(Collectors.toMap(GradeGroup::getId, GradeGroup::getGradeGroupName));
                    break;
                case 2:
                    //班级
                    relNameMap = sysClassService.listByIds(relIds).stream().collect(Collectors.toMap(SysClass::getId, SysClass::getClassName));
                    break;
                case 3:
                    //学生
                    relNameMap = studentService.listByIds(relIds).stream().collect(Collectors.toMap(StudentEntity::getId, StudentEntity::getChineseName));
                    break;
                case 5:
                    //部门
                    DepartmentEnum[] values = DepartmentEnum.values();
                    for (DepartmentEnum value : values)
                    {
                        relNameMap.put(value.getCode().longValue(), value.getDesc());
                    }
                    break;
                default:
                    break;
            }
            List<EnterpriseWechatSynRecordResModel> resModels = new ArrayList<>();
            for (EnterpriseWechatSynRecordEntity entity : list)
            {
                EnterpriseWechatSynRecordResModel model = new EnterpriseWechatSynRecordResModel();
                model.setRelId(entity.getRelId());
                model.setName(relNameMap.get(entity.getRelId()));
                model.setIncorrectReason(entity.getIncorrectReason());
                model.setTaskId(entity.getTaskId());
                resModels.add( model);
            }
            return Result.success(resModels);
        }
        return Result.success();
    }

}