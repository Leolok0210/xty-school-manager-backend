package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.dao.SchoolMajorDao;
import com.xiaotiyun.school.manager.model.entity.SchoolMajor;
import com.xiaotiyun.school.manager.model.entity.Subject;
import com.xiaotiyun.school.manager.model.req.SchoolMajorQueryReqModel;
import com.xiaotiyun.school.manager.model.req.SubjectRelGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolMajorDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import com.xiaotiyun.school.manager.model.res.SubjectRelResModel;
import com.xiaotiyun.school.manager.service.SchoolMajorService;
import com.xiaotiyun.school.manager.service.SubjectRelService;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.service.SubjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SchoolMajorServiceImpl extends ServiceImpl<SchoolMajorDao, SchoolMajor> implements SchoolMajorService {

    @Autowired
    private SchoolMajorDao schoolMajorDao;


    @Resource
    private SubjectService subjectService;
    @Override
    public void createSchoolMajors(List<SchoolMajor> schoolMajors) {
        schoolMajors.forEach(schoolMajor -> {
            schoolMajor.setCreateTime(LocalDateTime.now());
            schoolMajor.setUpdateTime(LocalDateTime.now());
            schoolMajor.setDeleted(0L);
        });
        saveBatch(schoolMajors);
    }

    @Override
    public void updateSchoolMajor(SchoolMajor schoolMajor) {
        schoolMajor.setUpdateTime(LocalDateTime.now());
        // 同一个学部下，专业名称不可重复
        //根据学部查询
        List<SchoolMajor> schoolMajors = schoolMajorDao.selectList(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId, schoolMajor.getSchoolId())
                .eq(SchoolMajor::getDepartmentId, schoolMajor.getDepartmentId())
                .eq(SchoolMajor::getDeleted, 0)
        );
        if(!CollectionUtils.isEmpty(schoolMajors)) {
            schoolMajors.forEach(item -> {
                if(item.getMajorName().equals(schoolMajor.getMajorName()) && !Objects.equals(item.getId(), schoolMajor.getId())) {
                    throw new BusinessException(LanguageConstants.MAJOR_NAME_EXISTS);
                }
            });
        }
        updateById(schoolMajor);
    }

    @Override
    public void deleteSchoolMajor(Long id) {
        //逻辑删除
        removeById(id);
    }

    @Override
    public SchoolMajor getSchoolMajorById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public PageInfo<SchoolMajorDetailResModel> getSchoolMajorList(SchoolMajorQueryReqModel reqModel) {
        //根据学科名称
        List<Long> subjectIds = null;
        if(StringUtils.isNotBlank(reqModel.getSubjectName()))
        {
            List<SubjectDetailResModel> relResModels = subjectService.getSubjects(reqModel.getSchoolId(),reqModel.getSubjectName());
            if(!CollectionUtils.isEmpty(relResModels))
            {
                subjectIds = relResModels.stream().map(SubjectDetailResModel::getId).collect(Collectors.toList());
            }
        }

        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
//        LambdaQueryWrapper<SchoolMajor> wrapper = new LambdaQueryWrapper<>();
//        wrapper.like(StringUtils.isNotBlank(reqModel.getMajorName()), SchoolMajor::getMajorName, reqModel.getMajorName())
//                .eq(reqModel.getSchoolId() != null, SchoolMajor::getSchoolId, reqModel.getSchoolId())
//                //find_in_set() 查询
//                .apply(subjectIds != null, "FIND_IN_SET({0}, major_subjects)", subjectIds)
//                .eq(SchoolMajor::getDeleted, 0);
        List<SchoolMajor> schoolMajors = this.baseMapper.getSchoolMajorList(reqModel, subjectIds);
        PageInfo<SchoolMajor> pageInfo = new PageInfo<>(schoolMajors);
        List<SchoolMajorDetailResModel> schoolMajorDetailResModels = pageInfo.getList().stream()
                .map(item -> {
                    SchoolMajorDetailResModel resModel = new SchoolMajorDetailResModel();
                    BeanUtils.copyProperties(item, resModel);
                    resModel.setDepartment(item.getDepartmentId());
                    return resModel;
                }).collect(Collectors.toList());
        PageInfo<SchoolMajorDetailResModel> schoolMajorDetailResModelPageInfo = new PageInfo<>(schoolMajorDetailResModels);
        schoolMajorDetailResModelPageInfo.setTotal(pageInfo.getTotal());
        schoolMajorDetailResModelPageInfo.setPages(pageInfo.getPages());
        schoolMajorDetailResModelPageInfo.setList(schoolMajorDetailResModels);
        return schoolMajorDetailResModelPageInfo;
    }

    @Override
    public List<SchoolMajor> getSchoolMajorByName(String name, Long schoolId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId, schoolId)
                .eq(SchoolMajor::getMajorName, name)
                .eq(SchoolMajor::getDeleted, 0)
        );
    }

    @Override
    public List<SchoolMajor> getSchoolMajorByDepartmentAndSchoolId(Integer departmentId, Long schoolId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId, schoolId)
                .eq(SchoolMajor::getDepartmentId, departmentId)
                .eq(SchoolMajor::getDeleted, 0)
        );
    }

    @Override
    public Long getMajorIdByName(String professional, Long schoolId) {
        List<SchoolMajor> schoolMajors = this.baseMapper.selectList(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId, schoolId)
                .eq(SchoolMajor::getMajorName, professional)
                .eq(SchoolMajor::getDeleted, 0)
        );
        if (!CollectionUtils.isEmpty(schoolMajors))
        {
            return schoolMajors.get(0).getId();
        }
        return null;
    }

    @Override
    public Map<String, SchoolMajor> getSchoolMajorMapBySchoolId(Long schoolId) {
        List<SchoolMajor> schoolMajors = this.baseMapper.selectList(new LambdaQueryWrapper<SchoolMajor>()
                .eq(SchoolMajor::getSchoolId, schoolId)
                .eq(SchoolMajor::getDeleted, 0)
        );
        if(CollectionUtils.isEmpty(schoolMajors))
        {
            return new HashMap<>();
        }
        return schoolMajors.stream().collect(Collectors.toMap(SchoolMajor::getMajorName, Function.identity(),(x1,x2) -> x1));
    }
}
