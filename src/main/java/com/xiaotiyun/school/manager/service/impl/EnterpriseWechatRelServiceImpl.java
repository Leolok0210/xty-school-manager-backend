package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaotiyun.school.manager.basic.enums.DepartmentEnum;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.WechatBusinessTypeEnum;
import com.xiaotiyun.school.manager.dao.*;
import com.xiaotiyun.school.manager.model.dto.CreateOrUpdateBatchDepartmentDTO;
import com.xiaotiyun.school.manager.model.dto.SynWxChatStatusUpdateDTO;
import com.xiaotiyun.school.manager.model.dto.WechatParentChildrenDTO;
import com.xiaotiyun.school.manager.model.dto.WechatParentInfoDTO;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatRelCheckReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatRelResModel;
import com.xiaotiyun.school.manager.model.res.StudentListResModel;
import com.xiaotiyun.school.manager.model.res.StudentParentResModel;
import com.xiaotiyun.school.manager.model.res.SysClassListResModel;
import com.xiaotiyun.school.manager.service.EnterpriseWechatRelService;
import com.xiaotiyun.school.manager.service.EnterpriseWechatService;
import com.xiaotiyun.school.manager.service.StudentParentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 企业微信关联关系表服务实现类
 */
@Slf4j
@Service
public class EnterpriseWechatRelServiceImpl extends ServiceImpl<EnterpriseWechatRelDao, EnterpriseWechatRelEntity> implements EnterpriseWechatRelService {


    @Resource
    private EnterpriseWechatService enterpriseWechatService;

    @Resource
    private GradeGroupMapper gradeGroupMapper;

    @Resource
    private SysClassDao sysClassDao;


    @Resource
    private StudentMapper studentMapper;

    @Resource
    private StudentParentDao studentParentDao;

    @Override
    public boolean saveBatchExist(List<EnterpriseWechatRelCheckReqModel> entities, EnterpriseWxChatTypeEnum type) {
        if (entities != null && !entities.isEmpty()) {
            List<EnterpriseWechatRelEntity> relEntities = entities.stream().map(entity -> {
                EnterpriseWechatRelEntity relEntity = new EnterpriseWechatRelEntity();
                BeanUtils.copyProperties(entity, relEntity);
                return relEntity;
            }).collect(Collectors.toList());

            super.saveBatch(relEntities);
            Long schoolId = relEntities.get(0).getSchoolId();
            String schoolYear = relEntities.get(0).getSchoolYear();
            //同步名称数据到微信
            List<CreateOrUpdateBatchDepartmentDTO> dtos = entities.stream().map(entity -> {
                CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = new CreateOrUpdateBatchDepartmentDTO();
                createOrUpdateBatchDepartmentDTO.setRelId(entity.getRelId());
                createOrUpdateBatchDepartmentDTO.setParentId(entity.getParentId());
                if (!StringUtils.isEmpty(entity.getWxId())){
                    createOrUpdateBatchDepartmentDTO.setWxId(entity.getWxId());
                }else {
                    createOrUpdateBatchDepartmentDTO.setWxId(entity.getRelId().toString());
                }
                return createOrUpdateBatchDepartmentDTO;
            }).collect(Collectors.toList());
            switch ( type) {
                case RELEVANCE_TYPE_LEVEL_GROUP:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,2, WechatBusinessTypeEnum.UPDATE,schoolYear);
                    break;
                case RELEVANCE_TYPE_CLASS:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,1, WechatBusinessTypeEnum.UPDATE,schoolYear);
                    break;
                case RELEVANCE_TYPE_STUDENT:
                    List<SynWxChatStatusUpdateDTO> updateDTOS  = entities.stream().map(entity -> {
                        SynWxChatStatusUpdateDTO updateDTO = new SynWxChatStatusUpdateDTO();
                        updateDTO.setRelId(entity.getRelId());
                        updateDTO.setThirdId(entity.getWxId());
                        return updateDTO;
                    }).collect(Collectors.toList());
                    enterpriseWechatService.createOrUpdateStudents(schoolId,updateDTOS,WechatBusinessTypeEnum.UPDATE,schoolYear);
                    break;
                case RELEVANCE_TYPE_SECTION:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,3, WechatBusinessTypeEnum.UPDATE,schoolYear);
                    break;
                default:
                    log.error("未定义的微信关联关系类型");
                    break;
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean saveBatchNoExist(List<EnterpriseWechatRelCheckReqModel> entities, EnterpriseWxChatTypeEnum type) {
        if (entities != null && !entities.isEmpty()) {
            List<EnterpriseWechatRelEntity> relEntities = entities.stream().map(entity -> {
                EnterpriseWechatRelEntity relEntity = new EnterpriseWechatRelEntity();
                BeanUtils.copyProperties(entity, relEntity);
                return relEntity;
            }).collect(Collectors.toList());

            super.saveBatch(relEntities);
            Long schoolId = relEntities.get(0).getSchoolId();
            String schoolYear = relEntities.get(0).getSchoolYear();
            //同步名称数据到微信
            List<CreateOrUpdateBatchDepartmentDTO> dtos = entities.stream().map(entity -> {
                CreateOrUpdateBatchDepartmentDTO createOrUpdateBatchDepartmentDTO = new CreateOrUpdateBatchDepartmentDTO();
                createOrUpdateBatchDepartmentDTO.setRelId(entity.getRelId());
                createOrUpdateBatchDepartmentDTO.setParentId(entity.getParentId());
                if (!StringUtils.isEmpty(entity.getWxId())){
                    createOrUpdateBatchDepartmentDTO.setWxId(entity.getWxId());
                }else {
                    createOrUpdateBatchDepartmentDTO.setWxId(entity.getRelId().toString());
                }
                return createOrUpdateBatchDepartmentDTO;
            }).collect(Collectors.toList());
            switch ( type) {
                case RELEVANCE_TYPE_LEVEL_GROUP:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,2, WechatBusinessTypeEnum.CREATE,schoolYear);
                    break;
                case RELEVANCE_TYPE_CLASS:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,1, WechatBusinessTypeEnum.CREATE,schoolYear);
                    break;
                case RELEVANCE_TYPE_STUDENT:
                    List<SynWxChatStatusUpdateDTO> updateDTOS  = entities.stream().map(entity -> {
                        SynWxChatStatusUpdateDTO updateDTO = new SynWxChatStatusUpdateDTO();
                        updateDTO.setRelId(entity.getRelId());
                        updateDTO.setThirdId(entity.getWxId());
                        return updateDTO;
                    }).collect(Collectors.toList());
                    enterpriseWechatService.createOrUpdateStudents(schoolId,updateDTOS,WechatBusinessTypeEnum.CREATE,schoolYear);
                    //同步家长
                    List<Long> studentIds = updateDTOS.stream().map(SynWxChatStatusUpdateDTO::getRelId).collect(Collectors.toList());
                    //updateDTOS tomap
                    Map<Long, SynWxChatStatusUpdateDTO> updateDTOSMap = updateDTOS.stream().collect(Collectors.toMap(SynWxChatStatusUpdateDTO::getRelId,
                            Function.identity(),(x1, x2) -> x1));
                    LambdaQueryWrapper<StudentParentEntity> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(StudentParentEntity::getStudentId, studentIds)
                            .eq(StudentParentEntity::getSchoolId, schoolId);
                    List<StudentParentEntity> studentParentEntities = studentParentDao.selectList(queryWrapper);
                    List<WechatParentInfoDTO> wechatParentInfoDTOList = new ArrayList<>();
                    List<EnterpriseWechatRelEntity> wechatRelEntities = new ArrayList<>();
                    for (StudentParentEntity studentParentEntity : studentParentEntities) {
                        if (!updateDTOSMap.containsKey(studentParentEntity.getStudentId()))
                        {
                            continue;
                        }
                        // 拼接企微同步数据
                        WechatParentInfoDTO wechatParentInfoDTO = new WechatParentInfoDTO();
                        wechatParentInfoDTO.setParent_userid(studentParentEntity.getId().toString());
                        wechatParentInfoDTO.setMobile(studentParentEntity.getParentPhone());
                        wechatParentInfoDTO.setTo_invite(false);
                        WechatParentChildrenDTO wechatParentChildrenDTO = new WechatParentChildrenDTO();
                        wechatParentChildrenDTO.setStudent_userid(updateDTOSMap.get(studentParentEntity.getStudentId()).getThirdId());
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
                    saveBatch(wechatRelEntities);
                    enterpriseWechatService.createOrUpdateParents(schoolId, wechatParentInfoDTOList, WechatBusinessTypeEnum.CREATE, schoolYear);
                    break;
                case RELEVANCE_TYPE_SECTION:
                    enterpriseWechatService.createOrUpdateBatchDepartment(schoolId,dtos,3, WechatBusinessTypeEnum.CREATE,schoolYear);
                    break;
                default:
                    log.error("未定义的微信关联关系类型");
                    break;
            }
            return true;
        }
        return false;
    }
    @Override
    public EnterpriseWechatRelEntity get(Long schoolId, Integer type, Long relId,String schoolYear) {
        LambdaQueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                .eq(EnterpriseWechatRelEntity::getType, type)
                .eq(EnterpriseWechatRelEntity::getSchoolYear, schoolYear)
                .eq(EnterpriseWechatRelEntity::getRelId, relId);
        return super.getOne(queryWrapper);
    }

    @Override
    public Map<Long, EnterpriseWechatRelEntity> list(Long schoolId, Integer type, List<Long> relIds,String schoolYear) {
        LambdaQueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                .eq(EnterpriseWechatRelEntity::getType, type)
                .eq(EnterpriseWechatRelEntity::getSchoolYear, schoolYear)
                .in(EnterpriseWechatRelEntity::getRelId, relIds);
        List<EnterpriseWechatRelEntity> list = super.list(queryWrapper);
        if (list != null && !list.isEmpty())
        {
            return list.stream().collect(Collectors.toMap(EnterpriseWechatRelEntity::getRelId, Function.identity(), (existing, replacement) -> existing));
        }
        return Collections.emptyMap();
    }

    @Override
    public List<EnterpriseWechatRelEntity> listByWxIds(Long schoolId, Integer type, List<String> wxIds,String schoolYear) {
        LambdaQueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                .eq(EnterpriseWechatRelEntity::getType, type)
                .eq(EnterpriseWechatRelEntity::getSchoolYear, schoolYear)
                .in(EnterpriseWechatRelEntity::getWxId, wxIds);
        return super.list(queryWrapper);
    }

    @Override
    public List<EnterpriseWechatRelResModel> list(Long schoolId,Integer type, Long groupId, Long classId,String schoolYear,
                                                  Integer  department) {
        List<Long> relIds = null;
        List<SysClassListResModel> sysClassListResModels = null;
        List<StudentListResModel> studentListResModels = null;
        List<GradeGroup> gradeGroups = null;
        if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS.getCode()))
        {
            //查询这个班级下面的
            sysClassListResModels = sysClassDao.selectSysClassListBySchoolIdAndSidAndGradeGroupId(schoolId, groupId, schoolYear);
            relIds = sysClassListResModels.stream().map(SysClassListResModel::getClassId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(sysClassListResModels))
            {
                return Collections.emptyList();
            }
        }else if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode()))
        {
            //查询这个班级下面的学生
            studentListResModels = studentMapper.listByClassId(classId);
            relIds = studentListResModels.stream().map(StudentListResModel::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(studentListResModels))
            {
                return Collections.emptyList();
            }
        }else if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP.getCode()))
        {
            LambdaQueryWrapper<GradeGroup> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GradeGroup::getDepartment, department)
                    .eq(GradeGroup::getSchoolId, schoolId);
            gradeGroups = gradeGroupMapper.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(gradeGroups))
            {
                return Collections.emptyList();
            }
            relIds = gradeGroups.stream().map(GradeGroup::getId).collect(Collectors.toList());
        }
        LambdaQueryWrapper<EnterpriseWechatRelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatRelEntity::getSchoolId, schoolId)
                .eq(EnterpriseWechatRelEntity::getSchoolYear, schoolYear)
                .in(!CollectionUtils.isEmpty(relIds),EnterpriseWechatRelEntity::getRelId, relIds)
                .eq(EnterpriseWechatRelEntity::getType, type);
        List<EnterpriseWechatRelEntity> list = this.list(queryWrapper);
        if (list != null && !list.isEmpty())
        {
            if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP.getCode()))
            {
                //查询级组名称
                //tomap
                Map<Long, GradeGroup> gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, Function.identity(),
                        (existing, replacement) -> existing));
                return list.stream().map(entity -> {
                    EnterpriseWechatRelResModel resModel = new EnterpriseWechatRelResModel();
                    resModel.setId(entity.getId());
                    GradeGroup gradeGroup = gradeGroupMap.get(entity.getRelId());
                    resModel.setName(gradeGroup == null ? "" : gradeGroup.getGradeGroupName());
                    resModel.setType(entity.getType());
                    resModel.setWxId(entity.getWxId());
                    resModel.setSchoolId(entity.getSchoolId());
                    resModel.setRelId(entity.getRelId());
                    return resModel;
                }).collect(Collectors.toList());
            }else if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS.getCode()))
            {
                //查询班级名称
                Map<Long, SysClassListResModel> gradeClassMap = sysClassListResModels.stream().collect(Collectors.toMap(SysClassListResModel::getClassId, Function.identity(),
                        (existing, replacement) -> existing));
                return list.stream().map(entity -> {
                    EnterpriseWechatRelResModel resModel = new EnterpriseWechatRelResModel();
                    resModel.setId(entity.getId());
                    SysClassListResModel gradeClass = gradeClassMap.get(entity.getRelId());
                    resModel.setName(gradeClass == null ? "" : gradeClass.getClassName());
                    resModel.setType(entity.getType());
                    resModel.setWxId(entity.getWxId());
                    resModel.setSchoolId(entity.getSchoolId());
                    resModel.setRelId(entity.getRelId());
                    return resModel;
                }).collect(Collectors.toList());
            }else if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode()))
            {
                Map<Long, StudentListResModel> studentMap = studentListResModels.stream().collect(Collectors.toMap(StudentListResModel::getId, Function.identity(),
                        (existing, replacement) -> existing));
                return list.stream().map(entity -> {
                    EnterpriseWechatRelResModel resModel = new EnterpriseWechatRelResModel();
                    resModel.setId(entity.getId());
                    StudentListResModel student = studentMap.get(entity.getRelId());
                    resModel.setName(student == null ? "" : student.getChineseName());
                    resModel.setType(entity.getType());
                    resModel.setWxId(entity.getWxId());
                    resModel.setSchoolId(entity.getSchoolId());
                    resModel.setRelId(entity.getRelId());
                    return resModel;
                }).collect(Collectors.toList());
            }else if (Objects.equals(type, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_SECTION.getCode()))
            {
                return list.stream().map(entity -> {
                    EnterpriseWechatRelResModel resModel = new EnterpriseWechatRelResModel();
                    resModel.setId(entity.getId());
                    DepartmentEnum departmentEnum = DepartmentEnum.getByCode(entity.getRelId().intValue());
                    resModel.setName(departmentEnum == null ? "" : departmentEnum.getDesc());
                    resModel.setType(entity.getType());
                    resModel.setWxId(entity.getWxId());
                    resModel.setSchoolId(entity.getSchoolId());
                    resModel.setRelId(entity.getRelId());
                    return resModel;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}