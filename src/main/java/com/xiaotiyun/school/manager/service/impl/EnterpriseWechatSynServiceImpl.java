package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.dao.EnterpriseWechatSynDao;
import com.xiaotiyun.school.manager.dao.UserDao;
import com.xiaotiyun.school.manager.model.entity.ClassroomEntity;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatSynEntity;
import com.xiaotiyun.school.manager.model.entity.UserEntity;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatSynResModel;
import com.xiaotiyun.school.manager.service.EnterpriseWechatSynService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 企业微信关联同步表服务实现类
 */
@Service
public class EnterpriseWechatSynServiceImpl extends ServiceImpl<EnterpriseWechatSynDao, EnterpriseWechatSynEntity> implements EnterpriseWechatSynService {


    @Resource
    private UserDao userDao;
    @Override
    public PageInfo<EnterpriseWechatSynResModel> list(Integer pageNum, Integer pageSize, Integer type) {
        LambdaQueryWrapper<EnterpriseWechatSynEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EnterpriseWechatSynEntity::getType, type);
        PageHelper.startPage(pageNum, pageSize);
        List<EnterpriseWechatSynEntity> list =
                this.list(queryWrapper);
        if (list == null || list.isEmpty())
        {
            return PageInfo.emptyPageInfo();
        }
        PageInfo<EnterpriseWechatSynEntity> pageInfo = new PageInfo<>(list);
        List<EnterpriseWechatSynEntity> entities = pageInfo.getList();
        List<Long> userIds = entities.stream().map(EnterpriseWechatSynEntity::getOpUserId).distinct().collect(Collectors.toList());
        List<UserEntity> userEntities = userDao.selectBatchIds(userIds);
        //tomap
        Map<Long, UserEntity> userMap = userEntities.stream().collect(Collectors.toMap(UserEntity::getId, user -> user));

        List<EnterpriseWechatSynResModel> resModels = entities.stream().map(item -> {
            EnterpriseWechatSynResModel resModel = new EnterpriseWechatSynResModel();
            resModel.setSchoolId(item.getSchoolId());
            resModel.setType(item.getType());
            resModel.setTotalCount(item.getTotalCount());
            resModel.setSuccessCount(item.getSuccessCount());
            resModel.setFailCount(item.getFailCount());
            resModel.setStatus(item.getStatus());
            resModel.setOpUserId(item.getOpUserId());
            resModel.setStartTime(item.getStartTime());
            resModel.setEndTime(item.getEndTime());
            UserEntity userEntity = userMap.get(item.getOpUserId());
            resModel.setOpUserName(userEntity == null ? "" : userEntity.getUsername());
            return resModel;
        }).collect(Collectors.toList());
        PageInfo<EnterpriseWechatSynResModel> resModelPageInfo = new PageInfo<>(resModels);
        return resModelPageInfo;
    }




}