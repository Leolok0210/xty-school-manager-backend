package com.xiaotiyun.school.manager.helper;

import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.WechatBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.model.dto.SynWxChatStatusUpdateDTO;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatRelEntity;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.entity.SysClass;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatRelCheckReqModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WxHelper {



    @Resource
    private SchoolWeixinRelevanceService schoolWeixinRelevanceService;

    @Resource
    private EnterpriseWechatService enterpriseWechatService;


    @Resource
    private EnterpriseWechatRelService enterpriseWechatRelService;


    @Resource
    private GradeGroupService gradeGroupService;

    @Resource
    private SysClassService sysClassService;




    public void createOrUpdateStudents (Long schoolId, List<Long> ids, WechatBusinessTypeEnum businessTypeEnum, String schoolYear)
    {
        if (StringUtils.isBlank(schoolYear))
        {
            schoolYear = SemesterUtils.getCurrentSemesterName(LocalDate.now());
        }
        try {
            boolean exist = schoolWeixinRelevanceService.exist(schoolId);
            if (!exist)
            {
                return;
            }
            Map<Long, EnterpriseWechatRelEntity> relEntityMap = enterpriseWechatRelService.list(schoolId,
                    EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode(), ids, schoolYear);
            List<SynWxChatStatusUpdateDTO> list = new ArrayList<>();
            List<EnterpriseWechatRelEntity> addList = new ArrayList<>();
            for (Long id : ids)
            {
                SynWxChatStatusUpdateDTO dto = new SynWxChatStatusUpdateDTO();
                dto.setRelId(id);
                if (relEntityMap.containsKey(id))
                {
                    dto.setThirdId(relEntityMap.get(id).getWxId());
                }else {
                    dto.setThirdId(id.toString());
                    EnterpriseWechatRelEntity entity = new EnterpriseWechatRelEntity();
                    entity.setSchoolId(schoolId);
                    entity.setWxId(id.toString());
                    entity.setType(EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT.getCode());
                    entity.setRelId(id);
                    entity.setSchoolYear(schoolYear);
                    addList.add(entity);
                }
                list.add(dto);
            }
            enterpriseWechatService.createOrUpdateStudents(schoolId, list,businessTypeEnum, schoolYear);
            if (CollectionUtils.isNotEmpty(addList))
            {
                enterpriseWechatRelService.saveOrUpdateBatch(addList);
            }
        }catch (Exception e)
        {
            log.error("同步学生信息到企业微信失败",e);
        }
    }


    public void crateOrUpdateParents(Long schoolId, List<Long> ids, String schoolYear,EnterpriseWxChatTypeEnum typeEnum)
    {
        if (StringUtils.isBlank(schoolYear))
        {
            schoolYear = SemesterUtils.getCurrentSemesterName(LocalDate.now());
        }
        try {
            boolean exist = schoolWeixinRelevanceService.exist(schoolId);
            if (!exist)
            {
                return;
            }
            Map<Long, EnterpriseWechatRelEntity> relEntityMap = enterpriseWechatRelService.list(schoolId,
                    typeEnum.getCode(), ids, schoolYear);
            List<EnterpriseWechatRelCheckReqModel> addList = new ArrayList<>();
            List<EnterpriseWechatRelCheckReqModel> updateList = new ArrayList<>();
            Map<Long,GradeGroup> gradeGroupMap = new HashMap<>();
            Map<Long,SysClass> classHashMap = new HashMap<>();
            if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP)
            {
                List<GradeGroup> gradeGroups = gradeGroupService.listByIds(ids);
                gradeGroupMap = gradeGroups.stream().collect(Collectors.toMap(GradeGroup::getId, gradeGroup -> gradeGroup));
            }else if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS)
            {
                List<SysClass> sysClasses = sysClassService.listByIds(ids);
                classHashMap = sysClasses.stream().collect(Collectors.toMap(SysClass::getId, sysClass -> sysClass));
            }
            for (Long id : ids)
            {
                EnterpriseWechatRelEntity relEntity = relEntityMap.get(id);
                EnterpriseWechatRelCheckReqModel reqModel = new EnterpriseWechatRelCheckReqModel();
                reqModel.setRelId(id);
                reqModel.setSchoolId(schoolId);
                reqModel.setType(typeEnum.getCode());
                reqModel.setSchoolYear(schoolYear);
                if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP)
                {
                    GradeGroup gradeGroup = gradeGroupMap.get(id);
                    EnterpriseWechatRelEntity relEntity1 = enterpriseWechatRelService.get(schoolId, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_SECTION.getCode(), gradeGroup.getDepartment(), schoolYear);
                    if (relEntity1 != null){
                        reqModel.setParentId(relEntity1.getWxId());
                    }
                }else if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS)
                {
                    SysClass sysClass = classHashMap.get(id);
                    EnterpriseWechatRelEntity relEntity1 = enterpriseWechatRelService.get(schoolId, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP.getCode(),
                            sysClass.getGradeGroup(), schoolYear);
                    if (relEntity1 != null){
                        reqModel.setParentId(relEntity1.getWxId());
                    }
                }
                if (relEntity != null){
                    reqModel.setWxId(relEntity.getWxId());
                    reqModel.setExist(1);
                    updateList.add(reqModel);
                }else {
                    reqModel.setWxId(id.toString());
                    reqModel.setExist(0);
                    addList.add(reqModel);
                }
            }
            if (!addList.isEmpty())
            {
                enterpriseWechatRelService.saveBatchNoExist(addList, typeEnum);
            }
            if (!updateList.isEmpty())
            {
                enterpriseWechatRelService.saveBatchExist(updateList, typeEnum);
            }
        }catch (Exception e)
        {
            log.error("同步部门信息到企业微信失败",e);
        }
    }


    public void delete(Long schoolId, List<Long> ids, EnterpriseWxChatTypeEnum typeEnum, String schoolYear)
    {
        if (StringUtils.isBlank(schoolYear))
        {
            schoolYear = SemesterUtils.getCurrentSemesterName(LocalDate.now());
        }
        try {
            boolean exist = schoolWeixinRelevanceService.exist(schoolId);
            if (!exist) {
                return;
            }
            Map<Long, EnterpriseWechatRelEntity> relEntityMap = enterpriseWechatRelService.list(schoolId,
                    typeEnum.getCode(), ids, schoolYear);
            if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_STUDENT)
            {
                List<Long> list = relEntityMap.values().stream().map(relEntity -> {
                    String wxId = relEntity.getWxId();
                    return Long.parseLong(wxId);
                }).collect(Collectors.toList());
                enterpriseWechatService.deleteStudents(schoolId,list,schoolYear);
            }else if (typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP ||
                    typeEnum == EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_CLASS){
                for (Long id : ids)
                {
                    EnterpriseWechatRelEntity relEntity = relEntityMap.get(id);
                    if (relEntity != null)
                    {
                        enterpriseWechatService.deleteDepartment(schoolId, Long.parseLong(relEntity.getWxId()), typeEnum.getCode(), schoolYear);
                    }
                }
            }
            enterpriseWechatRelService.removeBatchByIds(ids);
        }catch (Exception e)
        {
            log.error("同步部门信息到企业微信失败",e);
        }
    }
}
