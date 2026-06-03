package com.xiaotiyun.school.manager.controller.wx;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaotiyun.school.manager.basic.common.BasicController;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.model.dto.DepartmentResModelDTO;
import com.xiaotiyun.school.manager.model.dto.StudentInfoResModelDTO;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatRelEntity;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatRelCheckReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatRelResModel;
import com.xiaotiyun.school.manager.model.res.StudentInfoResModel;
import com.xiaotiyun.school.manager.model.res.WxDepartmentResModel;
import com.xiaotiyun.school.manager.service.EnterpriseWechatRelService;
import com.xiaotiyun.school.manager.service.EnterpriseWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企业微信关联关系表控制器
 */
@RestController
@RequestMapping("/api/enterprise/wechat/rel")
@Api(tags = "企业微信关联关系管理")
public class EnterpriseWechatRelController extends BasicController {

    @Resource
    private EnterpriseWechatRelService enterpriseWechatRelService;

    @Resource
    private EnterpriseWechatService enterpriseWechatService;
    
    /**
     * 新增企业微信关联关系
     * @param reqModel 企业微信关联关系实体
     * @return 是否新增成功
     */
    @PostMapping("/add")
    @ApiOperation("新增企业微信关联关系")
    public Result<Boolean> add(@RequestBody List<EnterpriseWechatRelCheckReqModel> reqModel) {
        if (reqModel == null || reqModel.isEmpty())
        {
            return Result.failed();
        }
        //产品要求全部清空
        Integer type1 = reqModel.get(0).getType();
        LambdaQueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatRelEntity::getSchoolId, getSchoolId())
                        .eq(EnterpriseWechatRelEntity::getType, type1);
        enterpriseWechatRelService.remove(queryWrapper);
        List<EnterpriseWechatRelCheckReqModel> entities = reqModel.stream().filter(item -> item.getExist() == 0).collect(Collectors.toList());
        if (!entities.isEmpty()){
            Integer type = entities.get(0).getType();
            EnterpriseWxChatTypeEnum messageByCode = EnterpriseWxChatTypeEnum.getMessageByCode(type);
            enterpriseWechatRelService.saveBatchExist(entities, messageByCode);
        }
        List<EnterpriseWechatRelCheckReqModel> noExistEntities = reqModel.stream().filter(item -> item.getExist() == 1).collect(Collectors.toList());
        if (!noExistEntities.isEmpty())
        {
            Integer type = noExistEntities.get(0).getType();
            EnterpriseWxChatTypeEnum messageByCode = EnterpriseWxChatTypeEnum.getMessageByCode(type);
            enterpriseWechatRelService.saveBatchNoExist(noExistEntities, messageByCode);
        }
        return Result.success(true);
    }
    /**
     * 查询关联信息
     * @param type 关联类型 1-级组 2-班级 3-学生 4-家长 5-学部
     * @param groupId 级组ID
     * @param classId 班级ID
     */
    @GetMapping("/list")
    @ApiOperation("查询关联信息")
    public Result<List<EnterpriseWechatRelResModel>> list(@RequestParam Integer type, @RequestParam(required = false) Long groupId,
                                                          @RequestParam(required = false) Long classId,@RequestParam String schoolYear,
                                                          @RequestParam(required = false) Integer  department) {
        return Result.success(enterpriseWechatRelService.list(getSchoolId(), type, groupId, classId,schoolYear,department));
    }



    /**
     * 查询企业微信的部门信息
     */
    @GetMapping("/getDepartmentList")
    @ApiOperation("查询企业微信的部门信息")
    public Result<List<WxDepartmentResModel>> getDepartmentList() {

        Long schoolId = getSchoolId();
        List<DepartmentResModelDTO> departmentList = enterpriseWechatService.getDepartmentList(schoolId, null);
        List<WxDepartmentResModel> wxDepartmentResModels = departmentList.stream().map(department -> {
            WxDepartmentResModel wxDepartmentResModel = new WxDepartmentResModel();
            wxDepartmentResModel.setDepartmentAdmins(department.getDepartment_admins());
            wxDepartmentResModel.setGraduated(department.getIs_graduated());
            wxDepartmentResModel.setId(department.getId());
            wxDepartmentResModel.setName(department.getName());
            wxDepartmentResModel.setOpenGroupChat(department.getOpen_group_chat());
            wxDepartmentResModel.setParentId(department.getParentid());
            wxDepartmentResModel.setType(department.getType());
            wxDepartmentResModel.setGroupChatId(department.getGroup_chat_id());
            return wxDepartmentResModel;
        }).collect(Collectors.toList());

        return Result.success(wxDepartmentResModels);
    }


    /**
     * 获取部门下的用户列表
     * @param id 班级id
     */
    @GetMapping("/getDepartmentUser")
    @ApiOperation("获取部门下的用户列表")
    public Result<List<StudentInfoResModel>> getDepartmentUser(@RequestParam Long id,@RequestParam String schoolYear) {

        Long schoolId = getSchoolId();
        EnterpriseWechatRelEntity rel = enterpriseWechatRelService.get(schoolId, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS.getCode(), id, schoolYear);
        List<StudentInfoResModelDTO> studentList = enterpriseWechatService.getDepartmentUser(schoolId, rel.getWxId());
        List<StudentInfoResModel> wxDepartmentResModels = studentList.stream().map(student -> {
            StudentInfoResModel wxDepartmentResModel = new StudentInfoResModel();
            wxDepartmentResModel.setWxIds(student.getStudent_userid());
            wxDepartmentResModel.setName(student.getName());
            return wxDepartmentResModel;
        }).collect(Collectors.toList());

        return Result.success(wxDepartmentResModels);
    }





}