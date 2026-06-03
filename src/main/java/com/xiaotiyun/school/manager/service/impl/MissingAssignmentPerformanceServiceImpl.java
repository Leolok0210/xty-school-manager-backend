package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.annotations.DataOperationLog;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import com.xiaotiyun.school.manager.basic.enums.DataBusinessTypeEnum;
import com.xiaotiyun.school.manager.basic.enums.DataOperationTypeEnum;
import com.xiaotiyun.school.manager.dao.MissingAssignmentPerformanceDao;
import com.xiaotiyun.school.manager.helper.UserAuthHelper;
import com.xiaotiyun.school.manager.model.entity.MissingAssignmentPerformance;
import com.xiaotiyun.school.manager.model.req.MissingAssignmentPerformanceQueryReqModel;
import com.xiaotiyun.school.manager.model.res.MissingAssignmentPerformanceDetailResModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
import com.xiaotiyun.school.manager.service.MissingAssignmentPerformanceService;
import com.xiaotiyun.school.manager.service.StudentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissingAssignmentPerformanceServiceImpl extends ServiceImpl<MissingAssignmentPerformanceDao, MissingAssignmentPerformance> implements MissingAssignmentPerformanceService {

    @Autowired
    private MissingAssignmentPerformanceDao missingAssignmentPerformanceDao;


    @Autowired
    private StudentService studentService;

    @Autowired
    private UserAuthHelper userAuthHelper;

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.CREATE, businessType = DataBusinessTypeEnum.MISSING_HOMEWORK)
    public List<MissingAssignmentPerformance> createMissingAssignmentPerformances(List<MissingAssignmentPerformance> missingAssignmentPerformances) {
        missingAssignmentPerformances.forEach(missingAssignmentPerformance -> {
            missingAssignmentPerformance.setCreateTime(LocalDateTime.now());
            missingAssignmentPerformance.setUpdateTime(LocalDateTime.now());
            missingAssignmentPerformance.setDeleted(0L);
        });
        saveBatch(missingAssignmentPerformances);
        return missingAssignmentPerformances;
    }

    @Override
    @DataOperationLog(opType = DataOperationTypeEnum.UPDATE, businessType = DataBusinessTypeEnum.MISSING_HOMEWORK)
    public MissingAssignmentPerformance updateMissingAssignmentPerformance(MissingAssignmentPerformance missingAssignmentPerformance) {
        missingAssignmentPerformance.setUpdateTime(LocalDateTime.now());
        updateById(missingAssignmentPerformance);
        return missingAssignmentPerformance;
    }

    @Override
    public void deleteMissingAssignmentPerformance(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public MissingAssignmentPerformance getMissingAssignmentPerformanceById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<MissingAssignmentPerformanceDetailResModel> getMissingAssignmentPerformanceList(MissingAssignmentPerformanceQueryReqModel reqModel) {
        Long studentId = null;
        if(StringUtils.isNoneBlank(reqModel.getStudentName()))
        {
            //根据学校id和学生姓名查询
            StudentResModel studentIdByNameAndSchoolId = studentService.getStudentIdByNameAndSchoolId(reqModel.getStudentName(), reqModel.getSchoolId());
            if(studentIdByNameAndSchoolId != null) {
                studentId = studentIdByNameAndSchoolId.getId();
            }else {
                return new PageInfo<>();
            }
        }
        boolean commonUser = userAuthHelper.getCommonUser(reqModel.getUserId(), reqModel.getSchoolId());
        List<Long> classIds = null;
        if (commonUser) {
            classIds = userAuthHelper.getUserClassIds(reqModel.getUserId(), reqModel.getSchoolId());
            if(CollectionUtils.isEmpty(classIds))
            {
                return new PageInfo<>();
            }
            reqModel.setClassIds(classIds);
        }
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        LambdaQueryWrapper<MissingAssignmentPerformance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNoneBlank(reqModel.getSid()), MissingAssignmentPerformance::getSid, reqModel.getSid())
                .eq(reqModel.getTerm() != null && reqModel.getTerm() > 0, MissingAssignmentPerformance::getTerm, reqModel.getTerm())
                .eq(reqModel.getClassId() != null && reqModel.getClassId() > 0, MissingAssignmentPerformance::getClassId, reqModel.getClassId())
                .eq(reqModel.getSchoolId() != null && reqModel.getSchoolId() > 0, MissingAssignmentPerformance::getSchoolId, reqModel.getSchoolId())
                .eq(studentId != null, MissingAssignmentPerformance::getStudentId, studentId)
                .eq(reqModel.getSubjectId() != null && reqModel.getSubjectId() > 0, MissingAssignmentPerformance::getSubjectId, reqModel.getSubjectId())
                .ge(reqModel.getStartDate() != null, MissingAssignmentPerformance::getDate, reqModel.getStartDate())
                .le(reqModel.getEndDate() != null, MissingAssignmentPerformance::getDate, reqModel.getEndDate())
                .in(reqModel.getClassIds() != null && !reqModel.getClassIds().isEmpty(), MissingAssignmentPerformance::getClassId, reqModel.getClassIds())
                .eq( MissingAssignmentPerformance::getDeleted, 0)
                .orderByDesc(BaseEntity::getCreateTime);
        List<MissingAssignmentPerformance> missingAssignmentPerformances = this.baseMapper.selectList(wrapper);
        PageInfo<MissingAssignmentPerformance> pageInfo = new PageInfo<>(missingAssignmentPerformances);
        List<MissingAssignmentPerformanceDetailResModel> missingAssignmentPerformanceDetailResModels = pageInfo.getList().stream()
                .map(item -> {
                    MissingAssignmentPerformanceDetailResModel resModel = new MissingAssignmentPerformanceDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<MissingAssignmentPerformanceDetailResModel> missingAssignmentPerformanceDetailResModelPageInfo = new PageInfo<>(missingAssignmentPerformanceDetailResModels);
        missingAssignmentPerformanceDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        missingAssignmentPerformanceDetailResModelPageInfo.setPages(pageInfo.getPages());
        missingAssignmentPerformanceDetailResModelPageInfo.setList(missingAssignmentPerformanceDetailResModels);
        return missingAssignmentPerformanceDetailResModelPageInfo;
    }

    @Override
    public boolean hasPerformance(Long periodId) {
        if(periodId == null){
            return false;
        }
        LambdaQueryWrapper<MissingAssignmentPerformance> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MissingAssignmentPerformance::getTerm,periodId);
        return this.count(queryWrapper) > 0 ;
    }
}