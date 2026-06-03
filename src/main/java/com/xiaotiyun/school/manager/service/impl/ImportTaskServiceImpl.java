package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.ImportTaskMapper;
import com.xiaotiyun.school.manager.model.dto.ImportTaskSaveDTO;
import com.xiaotiyun.school.manager.model.entity.ImportTaskEntity;
import com.xiaotiyun.school.manager.model.req.ImportTaskPageReqModel;
import com.xiaotiyun.school.manager.model.res.ImportTaskPageResModel;
import com.xiaotiyun.school.manager.service.ImportTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ImportTaskServiceImpl extends ServiceImpl<ImportTaskMapper, ImportTaskEntity> implements ImportTaskService {

    @Override
    public PageInfo<ImportTaskPageResModel> page(ImportTaskPageReqModel reqModel) {
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());

        // 1. 构建查询条件
        LambdaQueryWrapper<ImportTaskEntity> wrapper = new LambdaQueryWrapper<ImportTaskEntity>()
                .eq(ImportTaskEntity::getSchoolId, reqModel.getSchoolId())
                .in(CollectionUtils.isNotEmpty(reqModel.getTypes()), ImportTaskEntity::getType, reqModel.getTypes())
                .eq(ImportTaskEntity::getDeleted, 0L);

        // 2. 查询数据
        List<ImportTaskEntity> list = list(wrapper.orderByDesc(ImportTaskEntity::getCreateTime));
        PageInfo<ImportTaskEntity> pageInfo = new PageInfo<>(list);
        // 3. 转换返回结果
        List<ImportTaskPageResModel> resList = list.stream().map((ImportTaskEntity entity) -> {
            ImportTaskPageResModel resModel = new ImportTaskPageResModel();
            BeanUtils.copyProperties(entity, resModel);
            return resModel;
        }).collect(Collectors.toList());
        PageInfo<ImportTaskPageResModel> result = new PageInfo<>(resList);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ImportTaskSaveDTO dto) {
        ImportTaskEntity entity = BeanConvertUtil.convert(dto, ImportTaskEntity.class);
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ImportTaskSaveDTO dto) {
        ImportTaskEntity entity = this.getById(id);
        if (entity != null) {
            // 使用BeanUtils替代BeanConvertUtil
            BeanUtils.copyProperties(dto, entity);
            this.updateById(entity);
        }
    }
}