package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.enums.WechatBusinessTypeEnum;
import com.xiaotiyun.school.manager.dao.StudentParentDao;
import com.xiaotiyun.school.manager.model.dto.WechatParentChildrenDTO;
import com.xiaotiyun.school.manager.model.dto.WechatParentInfoDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.StudentParentAddReqModel;
import com.xiaotiyun.school.manager.model.res.StudentParentResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学生家长信息Service实现类
 */
@Service
@RequiredArgsConstructor
public class StudentParentServiceImpl extends ServiceImpl<StudentParentDao, StudentParentEntity> implements StudentParentService {

    private final SchoolService schoolService;

    private final EnterpriseWechatService enterpriseWechatService;
    private final StudentService studentService;
    private final SysClassService classService;
    private final EnterpriseWechatRelService enterpriseWechatRelService;

    /**
     * 新增或更新学生家长信息
     *
     * @param reqModels  学生家长信息新增请求模型列表
     * @param schoolId   学校ID
     * @param schoolYear
     * @return 结果
     */
    @Override
    public Result addOrUpdate(List<StudentParentAddReqModel> reqModels, Long schoolId, String schoolYear) {
        List<StudentParentEntity> insertList = new ArrayList<>();
        List<StudentParentEntity> updateList = new ArrayList<>();
        for (StudentParentAddReqModel reqModel : reqModels) {
            if (reqModel.getId() == null || reqModel.getId().equals(0L)) {
                insertList.add(BeanUtil.copyProperties(reqModel, StudentParentEntity.class));
            } else {
                updateList.add(BeanUtil.copyProperties(reqModel, StudentParentEntity.class));
            }
        }
        if (!insertList.isEmpty()) {
            this.saveBatch(insertList);
        }
        if (!updateList.isEmpty()) {
            this.updateBatchById(updateList);
        }
        // 若学校有绑定企微，需要更新家长数据
        SchoolEntity school = schoolService.getById(schoolId);
        if (StringUtil.isNotEmpty(school.getEntWechatName())) {
            if (!insertList.isEmpty() || !updateList.isEmpty()){
                if (StringUtils.isEmpty(schoolYear)) {
                    StudentEntity student = studentService.getById(reqModels.get(0).getStudentId());
                    if (student == null) {
                        return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.STUDENT_NOT_EXIST);
                    }
                    SysClass sysClass = classService.getById(student.getClassId());
                    if (sysClass == null) {
                        return Result.failed(ResultCode.FAILED.getCode(), LanguageConstants.CLASS_ID_REQUIRED);
                    }
                    schoolYear = sysClass.getSid();
                }
                try {
                    syncEntWechatParent(schoolId, schoolYear, insertList, updateList, reqModels);
                } catch (Exception e) {
                    log.error("同步企业微信家长信息失败", e);
                }
            }
        }
        return Result.success();
    }

    private void syncEntWechatParent(Long schoolId, String schoolYear, List<StudentParentEntity> insertList, List<StudentParentEntity> updateList, List<StudentParentAddReqModel> reqModels) {
        Set<Long> studentId = reqModels.stream().map(StudentParentAddReqModel::getStudentId).collect(Collectors.toSet());
        List<EnterpriseWechatRelEntity> triUsers = enterpriseWechatRelService.list(Wrappers.<EnterpriseWechatRelEntity>lambdaQuery()
                .eq(EnterpriseWechatRelEntity::getType, 3)
                .eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                .in(EnterpriseWechatRelEntity::getRelId, studentId));
        Map<Long, EnterpriseWechatRelEntity> stuIdRelMap = new HashMap<>();
        if (!triUsers.isEmpty()) {
            stuIdRelMap = triUsers.stream().collect(Collectors.toMap(EnterpriseWechatRelEntity::getRelId, Function.identity()));
        }
        if (!insertList.isEmpty()) {
            List<WechatParentInfoDTO> wechatParentInfoDTOList = new ArrayList<>();
            List<EnterpriseWechatRelEntity> wechatRelEntities = new ArrayList<>();
            for (StudentParentEntity studentParentEntity : insertList) {
                if (stuIdRelMap.containsKey(studentParentEntity.getStudentId())) {
                    continue;
                }
                // 拼接企微同步数据
                WechatParentInfoDTO wechatParentInfoDTO = new WechatParentInfoDTO();
                wechatParentInfoDTO.setParent_userid(studentParentEntity.getId().toString());
                wechatParentInfoDTO.setMobile(studentParentEntity.getParentPhone());
                wechatParentInfoDTO.setTo_invite(false);
                WechatParentChildrenDTO wechatParentChildrenDTO = new WechatParentChildrenDTO();
                wechatParentChildrenDTO.setStudent_userid(stuIdRelMap.get(studentParentEntity.getStudentId()).getWxId());
                wechatParentChildrenDTO.setRelation(studentParentEntity.getParentRelation());
                wechatParentInfoDTO.setChildren(Collections.singletonList(wechatParentChildrenDTO));
                wechatParentInfoDTOList.add(wechatParentInfoDTO);
                // 拼接企微关联表数据
                EnterpriseWechatRelEntity wechatRelEntity = new EnterpriseWechatRelEntity();
                wechatRelEntity.setSchoolId(schoolId);
                wechatRelEntity.setSchoolYear(schoolYear);
                wechatRelEntity.setType(4);
                wechatRelEntity.setRelId(studentParentEntity.getId());
                wechatRelEntity.setWxId(studentParentEntity.getId().toString());
                wechatRelEntities.add(wechatRelEntity);
            }
            enterpriseWechatRelService.saveBatch(wechatRelEntities);
            enterpriseWechatService.createOrUpdateParents(schoolId, wechatParentInfoDTOList, WechatBusinessTypeEnum.CREATE, schoolYear);
        }
        if (!updateList.isEmpty()) {
            Set<Long> parentId = updateList.stream().map(StudentParentEntity::getId).collect(Collectors.toSet());
            List<EnterpriseWechatRelEntity> wechatRelEntities = enterpriseWechatRelService.list(Wrappers.<EnterpriseWechatRelEntity>lambdaQuery()
                    .eq(EnterpriseWechatRelEntity::getType, 4)
                    .eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                    .in(EnterpriseWechatRelEntity::getRelId, parentId));
            Map<Long, EnterpriseWechatRelEntity> parentIdRelMap = wechatRelEntities.stream().collect(Collectors.toMap(EnterpriseWechatRelEntity::getRelId, Function.identity()));
            List<WechatParentInfoDTO> wechatParentInfoDTOList = new ArrayList<>();
            for (StudentParentEntity studentParentEntity : updateList) {
                if (!stuIdRelMap.containsKey(studentParentEntity.getStudentId()) ||
                        !parentIdRelMap.containsKey(studentParentEntity.getId())) {
                    continue;
                }
                WechatParentInfoDTO wechatParentInfoDTO = new WechatParentInfoDTO();
                wechatParentInfoDTO.setParent_userid(parentIdRelMap.get(studentParentEntity.getId()).getWxId());
                wechatParentInfoDTO.setMobile(studentParentEntity.getParentPhone());
                wechatParentInfoDTO.setTo_invite(false);
                WechatParentChildrenDTO wechatParentChildrenDTO = new WechatParentChildrenDTO();
                wechatParentChildrenDTO.setStudent_userid(stuIdRelMap.get(studentParentEntity.getStudentId()).getWxId());
                wechatParentChildrenDTO.setRelation(studentParentEntity.getParentRelation());
                wechatParentInfoDTO.setChildren(Collections.singletonList(wechatParentChildrenDTO));
                wechatParentInfoDTOList.add(wechatParentInfoDTO);
            }
            enterpriseWechatService.createOrUpdateParents(schoolId, wechatParentInfoDTOList, WechatBusinessTypeEnum.UPDATE, schoolYear);
        }
    }

    @Override
    public Result<List<StudentParentResModel>> listByStudentId(Long studentId, Long schoolId) {
        List<StudentParentEntity> entityList = this.baseMapper.selectList(new LambdaQueryWrapper<StudentParentEntity>()
                .eq(StudentParentEntity::getSchoolId, schoolId)
                .eq(StudentParentEntity::getStudentId, studentId));
        if (entityList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        List<StudentParentResModel> resModelList = BeanUtil.copyToList(entityList, StudentParentResModel.class);
        return Result.success(resModelList);
    }

    @Override
    public List<StudentParentResModel> listByStudentIds(List<Long> studentIds, Long schoolId) {
        LambdaQueryWrapper<StudentParentEntity> queryWrapper = new LambdaQueryWrapper<StudentParentEntity>()
                .eq(StudentParentEntity::getSchoolId, schoolId)
                .in(StudentParentEntity::getStudentId, studentIds);
      return BeanUtil.copyToList(this.baseMapper.selectList(queryWrapper), StudentParentResModel.class);
    }
}
