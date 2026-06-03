package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.dao.LeisureCourseOpRecordDao;
import com.xiaotiyun.school.manager.model.entity.LeisureCourseOpRecordEntity;
import com.xiaotiyun.school.manager.model.req.LeisureCourseOpRecordQuery;
import com.xiaotiyun.school.manager.model.res.LeisureCourseOpRecordRes;
import com.xiaotiyun.school.manager.service.LeisureCourseOpRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeisureCourseOpRecordServiceImpl extends ServiceImpl<LeisureCourseOpRecordDao, LeisureCourseOpRecordEntity> implements LeisureCourseOpRecordService {

    @Override
    public PageInfo<LeisureCourseOpRecordRes> page(LeisureCourseOpRecordQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<LeisureCourseOpRecordEntity> where = Wrappers.<LeisureCourseOpRecordEntity>lambdaQuery()
                .eq(LeisureCourseOpRecordEntity::getSourceId, query.getSourceId());
        if (ObjectUtils.isNotEmpty(query.getActivityId())) {
            where.eq(LeisureCourseOpRecordEntity::getActivityId, query.getActivityId());
        }
        if (ObjectUtils.isNotEmpty(query.getCourseId())) {
            where.eq(LeisureCourseOpRecordEntity::getCoursesId, query.getCourseId());
        }
        where.orderByDesc(LeisureCourseOpRecordEntity::getCreateTime);
        List<LeisureCourseOpRecordEntity> list = this.list(where);
        if (ObjectUtils.isNotEmpty(list)) {
            List<LeisureCourseOpRecordRes> resList = list.stream().map(a -> {
                LeisureCourseOpRecordRes res = new LeisureCourseOpRecordRes();
                BeanUtils.copyProperties(a, res);
                return res;
            }).collect(Collectors.toList());
            return new PageInfo<>(resList);
        }
        return new PageInfo<>(new ArrayList<>());
    }
}
