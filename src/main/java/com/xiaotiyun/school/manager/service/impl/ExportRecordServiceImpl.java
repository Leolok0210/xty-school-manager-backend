package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.ExportRecord;
import com.xiaotiyun.school.manager.model.res.ExportRecordResModel;
import com.xiaotiyun.school.manager.service.ExportRecordService;
import com.xiaotiyun.school.manager.dao.ExportRecordMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Akame
* @description 针对表【export_record(导出记录表)】的数据库操作Service实现
* @createDate 2025-09-10 18:05:54
*/
@Service
public class ExportRecordServiceImpl extends ServiceImpl<ExportRecordMapper, ExportRecord>
    implements ExportRecordService{

    @Override
    public PageInfo<ExportRecordResModel> list(int pageNum, int pageSize, List<Long> relId,Long schoolId, Integer type) {
        LambdaQueryWrapper<ExportRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ExportRecord::getRelId, relId);
        queryWrapper.eq(ExportRecord::getSchoolId, schoolId);
        queryWrapper.eq(ExportRecord::getType, type);
        PageHelper.startPage(pageNum, pageSize);
        List<ExportRecord> list = this.list(queryWrapper);
        PageInfo<ExportRecord> pageInfo = new PageInfo<>(list);
        if (!CollectionUtils.isEmpty(list)) {
            List<ExportRecordResModel> resModels = list.stream().map(item -> {
                ExportRecordResModel resModel = new ExportRecordResModel();
                BeanUtils.copyProperties(item, resModel);
                return resModel;
            }).collect(Collectors.toList());
            PageInfo<ExportRecordResModel> result = new PageInfo<>(resModels);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return new PageInfo<>();
    }
}




