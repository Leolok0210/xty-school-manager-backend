package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.basic.enums.ResultCode;
import com.xiaotiyun.school.manager.basic.util.LanguageUtil;
import com.xiaotiyun.school.manager.dao.StudentHealthDeclareDao;
import com.xiaotiyun.school.manager.model.entity.StudentHealthDeclareEntity;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclarePageReqModel;
import com.xiaotiyun.school.manager.model.res.StudentHealthDeclarePageResModel;
import com.xiaotiyun.school.manager.service.StudentFamilyService;
import com.xiaotiyun.school.manager.service.StudentHealthDeclareService;
import com.xiaotiyun.school.manager.service.StudentMedicalAttentionService;
import com.xiaotiyun.school.manager.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生健康申报表服务实现类
 * @author generated
 * @since 2025-8-26
 */
@Service
@RequiredArgsConstructor
public class StudentHealthDeclareServiceImpl extends ServiceImpl<StudentHealthDeclareDao, StudentHealthDeclareEntity> implements StudentHealthDeclareService {

    private final StudentService studentService;
    private final StudentFamilyService studentFamilyService;
    private final StudentMedicalAttentionService studentMedicalAttentionService;

    private final LanguageUtil languageUtil;

    @Override
    public PageInfo<StudentHealthDeclarePageResModel> page(StudentHealthDeclarePageReqModel pageReqModel) {
        // 拼接参数
        QueryWrapper<StudentHealthDeclareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("student_id", pageReqModel.getStudentId());
        // 分页查询
        PageHelper.startPage(pageReqModel.getPageNum(), pageReqModel.getPageSize());
        List<StudentHealthDeclareEntity> list = this.list(queryWrapper);
        List<StudentHealthDeclarePageResModel> resList = list.stream().map(entity -> {
            StudentHealthDeclarePageResModel resModel = new StudentHealthDeclarePageResModel();
            // 转换为分页结果
            resModel.setId(entity.getId());
            resModel.setSchoolYear(entity.getSchoolYear());
            resModel.setSubmitTime(entity.getCreateTime());
            return resModel;
        }).collect(Collectors.toList());
        // 转换为分页结果
        PageInfo<StudentHealthDeclarePageResModel> pageInfo = new PageInfo<>(resList);
        return pageInfo;
    }

    @Override
    public Result addRecord(StudentHealthDeclareAddReqModel reqModel) {
        // 校验参数
        if (reqModel.getIntention() != 1 && StringUtils.isBlank(reqModel.getProveImgUrl())) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.PARAM_ERROR));
        }
        // 每个学年只能提交一次
        QueryWrapper<StudentHealthDeclareEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        queryWrapper.eq("student_id", reqModel.getStudentId());
        queryWrapper.eq("school_year", reqModel.getSchoolYear());
        List<StudentHealthDeclareEntity> list = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            return Result.failed(ResultCode.FAILED.getCode(), languageUtil.getMessage(LanguageConstants.HEALTH_DECLARE_EACH_YEAR_ONLY_ONCE));
        }
        // 保存记录
        StudentHealthDeclareEntity entity = new StudentHealthDeclareEntity();
        BeanUtils.copyProperties(reqModel, entity);
        this.save(entity);
        // 更新学生基础信息
        studentService.updateStudentByHealthDeclare(reqModel);
        // 更新医疗事项
        studentMedicalAttentionService.updateByHealthDeclare(reqModel);
        // 更新紧急联络人
        studentFamilyService.updateByHealthDeclare(reqModel);
        return Result.success();
    }
}
