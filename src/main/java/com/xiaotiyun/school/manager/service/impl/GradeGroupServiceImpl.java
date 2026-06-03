package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.EnterpriseWxChatTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.WechatBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.SemesterUtils;
import com.xiaotiyun.school.manager.dao.GradeGroupMapper;
import com.xiaotiyun.school.manager.helper.WxHelper;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.xiaotiyun.school.manager.model.req.GradeGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.GradeGroupDetailResModel;
import com.xiaotiyun.school.manager.service.GradeGroupService;
import com.xiaotiyun.school.manager.service.SysClassService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author Akame
* @description 针对表【grade_group( 级组表)】的数据库操作Service实现
* @createDate 2025-02-11 16:47:23
*/
@Service
public class GradeGroupServiceImpl extends ServiceImpl<GradeGroupMapper, GradeGroup>
    implements GradeGroupService {

    @Autowired
    private SysClassService sysClassService;



    @Resource
    private WxHelper wxHelper;
    @Override
    public PageInfo<GradeGroupDetailResModel> getGradeGroupList(GradeGroupQueryReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<GradeGroup> wrapper = getQueryWrapper(reqModel);
        wrapper.orderByAsc(GradeGroup::getId);
        List<GradeGroup> gradeGroups = list(wrapper);
        PageInfo<GradeGroup> gradeGroupPageInfo = new PageInfo<>(gradeGroups);
        List<GradeGroupDetailResModel> resModels = gradeGroupPageInfo.getList().stream()
                .map(gradeGroup -> {
                    GradeGroupDetailResModel resModel = new GradeGroupDetailResModel();
                    BeanUtils.copyProperties(gradeGroup, resModel);
                    resModel.setList(sysClassService.getSysClassListBySchoolIdAndSidAndGradeGroupId(reqModel.getSchoolId(), gradeGroup.getId(),reqModel.getSid()));
                    return resModel;
                })
                .collect(Collectors.toList());
        PageInfo<GradeGroupDetailResModel> pageInfo = new PageInfo<>(resModels);
        pageInfo.setList(resModels);
        pageInfo.setTotal(gradeGroupPageInfo.getTotal());
        pageInfo.setPages(gradeGroupPageInfo.getPages());
        return pageInfo;
    }

    @Override
    public List<GradeGroup> getGradeAllGroupList(GradeGroupQueryReqModel reqModel) {
        return list(getQueryWrapper(reqModel));
    }

    @Override
    public GradeGroup getGradeGroupByName(String gradeGroup, Long schoolId) {
        return getOne(new LambdaQueryWrapper<GradeGroup>()
                .eq(GradeGroup::getGradeGroupName, gradeGroup)
                .eq(GradeGroup::getSchoolId, schoolId)
                .eq(GradeGroup::getDeleted, 0));
    }

    @Override
    public void createGradeGroups(List<GradeGroup> gradeGroups) {
        //校验级组名是否重复
        gradeGroups.forEach(gradeGroup -> {
            if (getGradeGroupByName(gradeGroup.getGradeGroupName(), gradeGroup.getSchoolId()) != null) {
                throw new BusinessException(LanguageConstants.GRADE_GROUP_NAME_DUPLICATE);
            }
        });
        //gradeGroups 里面的级组名校验
        Map<String, GradeGroup> gradeGroupMap = new HashMap<>();
        gradeGroups.forEach(gradeGroup -> {
            if (gradeGroup.getProfessionalSubject() != null && ((gradeGroup.getProfessionalSubject() == 2
                    || gradeGroup.getProfessionalSubject() == 3) && gradeGroup.getArtsScienceType() == null))
            {
                throw new BusinessException(LanguageConstants.PARAM_ERROR);
            }
            if (gradeGroupMap.containsKey(gradeGroup.getGradeGroupName())) {
                throw new BusinessException(LanguageConstants.GRADE_GROUP_NAME_DUPLICATE);
            }
            gradeGroupMap.put(gradeGroup.getGradeGroupName(), gradeGroup);
        });
        saveBatch(gradeGroups);
        String currentSemesterName = SemesterUtils.getCurrentSemesterName(LocalDate.now());
        wxHelper.crateOrUpdateParents(gradeGroups.get(0).getSchoolId(), gradeGroups.stream().map(GradeGroup::getId).collect(Collectors.toList()),
                currentSemesterName, EnterpriseWxChatTypeEnum.RELEVANCE_TYPE_LEVEL_GROUP);
    }

    @Override
    public Map<Long, String> getNamesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }

        // 查询班级信息
        List<GradeGroup> gradeGroups = this.baseMapper.selectList(
                new LambdaQueryWrapper<GradeGroup>()
                        .in(GradeGroup::getId, ids)
                        .eq(GradeGroup::getDeleted, 0));

        // 转换为Map
        return gradeGroups.stream()
                .collect(Collectors.toMap(
                        GradeGroup::getId,
                        GradeGroup::getGradeGroupName,
                        (v1, v2) -> v1));  // 如果有重复key,保留第一个值
    }

    private LambdaQueryWrapper<GradeGroup> getQueryWrapper(GradeGroupQueryReqModel reqModel) {
        LambdaQueryWrapper<GradeGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GradeGroup::getDeleted, 0);
        if (reqModel.getDepartment() != null && reqModel.getDepartment() > 0) {
            queryWrapper.eq(GradeGroup::getDepartment, reqModel.getDepartment());
        }
        if (StringUtils.isNoneBlank(reqModel.getGradeGroupName())) {
            queryWrapper.eq(GradeGroup::getGradeGroupName, reqModel.getGradeGroupName());
        }
        if (reqModel.getSchoolId() != null && reqModel.getSchoolId() > 0) {
            queryWrapper.eq(GradeGroup::getSchoolId, reqModel.getSchoolId());
        }
        if (StringUtils.isNoneBlank(reqModel.getGrade())) {
            queryWrapper.eq(GradeGroup::getGrade, reqModel.getGrade());
        }
        if (CollectionUtils.isNotEmpty(reqModel.getIds())) {
            queryWrapper.in(GradeGroup::getId, reqModel.getIds());
        }
        return queryWrapper;
    }
}