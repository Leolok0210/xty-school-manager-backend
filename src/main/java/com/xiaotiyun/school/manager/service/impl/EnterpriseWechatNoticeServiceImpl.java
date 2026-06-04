package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.enums.FileRelevanceTypeEnum;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.EnterpriseWechatNoticeDao;
import com.xiaotiyun.school.manager.model.entity.EnterpriseWechatNoticeEntity;
import com.xiaotiyun.school.manager.model.entity.SysFileEntity;
import com.xiaotiyun.school.manager.model.entity.SysFileRelevanceEntity;
import com.xiaotiyun.school.manager.model.entity.UserSchoolRelEntity;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticePageReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeSaveReqModel;
import com.xiaotiyun.school.manager.model.req.EnterpriseWechatNoticeUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.EnterpriseWechatNoticeResModel;
import com.xiaotiyun.school.manager.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 企业微信通知Service层实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseWechatNoticeServiceImpl extends ServiceImpl<EnterpriseWechatNoticeDao, EnterpriseWechatNoticeEntity> implements EnterpriseWechatNoticeService {
    private final SysFileRelevanceService sysFileRelevanceService;
    private final SysFileService sysFileService;
    private final UserSchoolRelService userSchoolRelService;
    @Lazy
    private final NoticeService noticeService;

    /**
     * 分页查询企业微信通知列表
     */
    @Override
    public PageInfo<EnterpriseWechatNoticeResModel> page(Long schoolId, EnterpriseWechatNoticePageReqModel reqModel) {
        // 设置分页参数
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        // 构建查询条件
        LambdaQueryWrapper<EnterpriseWechatNoticeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EnterpriseWechatNoticeEntity::getSchoolId, schoolId)
                .eq(reqModel.getNoticeType() != null && reqModel.getNoticeType() > 0, EnterpriseWechatNoticeEntity::getNoticeType, reqModel.getNoticeType())
                .orderByDesc(EnterpriseWechatNoticeEntity::getCreateTime);
        List<EnterpriseWechatNoticeEntity> list = this.list(wrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            PageInfo<EnterpriseWechatNoticeEntity> pageInfo = new PageInfo<>(list);
            //获取用户信息
            List<Long> userIds = list.stream().map(EnterpriseWechatNoticeEntity::getCreatorId).distinct().collect(Collectors.toList());
            QueryWrapper<UserSchoolRelEntity> userWrapper = new QueryWrapper<>();
            userWrapper.lambda().in(UserSchoolRelEntity::getUserId, userIds)
                    .eq(UserSchoolRelEntity::getSchoolId, schoolId);
            List<UserSchoolRelEntity> userSchoolRelEntities = userSchoolRelService.list(userWrapper);
            Map<Long, UserSchoolRelEntity> userMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userSchoolRelEntities)) {
                userMap = userSchoolRelEntities.stream().collect(Collectors.toMap(UserSchoolRelEntity::getUserId, userSchoolRelEntity -> userSchoolRelEntity));
            }
            //获取文件关联信息
            Map<Long, List<SysFileRelevanceEntity>> fileRelevanceMap = new HashMap<>();
            Map<Long, SysFileEntity> fileMap = new HashMap<>();
            List<Long> ids = list.stream().map(EnterpriseWechatNoticeEntity::getId).collect(Collectors.toList());
            QueryWrapper<SysFileRelevanceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SysFileRelevanceEntity::getBusinessId, ids)
                    .eq(SysFileRelevanceEntity::getSchoolId, schoolId)
                    .eq(SysFileRelevanceEntity::getType, FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());
            List<SysFileRelevanceEntity> fileRelevanceList = sysFileRelevanceService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(fileRelevanceList)) {
                // 获取文件信息
                List<Long> fileIds = fileRelevanceList.stream().map(SysFileRelevanceEntity::getFileId).collect(Collectors.toList());
                fileMap = sysFileService.listByIds(fileIds).stream().collect(Collectors.toMap(SysFileEntity::getId, Function.identity()));
                // 按业务ID分组关联关系
                fileRelevanceMap = fileRelevanceList.stream().collect(Collectors.groupingBy(SysFileRelevanceEntity::getBusinessId));
            }
            // 转换结果
            List<EnterpriseWechatNoticeResModel> resList = new ArrayList<>();
            for (EnterpriseWechatNoticeEntity entity : list) {
                EnterpriseWechatNoticeResModel resModel = new EnterpriseWechatNoticeResModel();
                BeanUtils.copyProperties(entity, resModel);
                List<SysFileRelevanceEntity> relevanceList = fileRelevanceMap.get(entity.getId());
                if (CollectionUtils.isNotEmpty(relevanceList)) {
                    List<String> files = new ArrayList<>();
                    for (SysFileRelevanceEntity relevance : relevanceList) {
                        SysFileEntity sysFileEntity = fileMap.get(relevance.getFileId());
                        if (sysFileEntity != null) {
                            files.add(sysFileEntity.getPath());
                        }
                    }
                    resModel.setFiles(files);
                }
                UserSchoolRelEntity userSchoolRel = userMap.get(entity.getCreatorId());
                if (userSchoolRel != null) {
                    resModel.setCreatorName(userSchoolRel.getUsername());
                }
                resList.add(resModel);
            }
            // 4. 创建新的PageInfo并复制分页信息
            PageInfo<EnterpriseWechatNoticeResModel> result = new PageInfo<>(resList);
            result.setTotal(pageInfo.getTotal());
            result.setPages(pageInfo.getPages());
            return result;
        }
        return null;
    }

    /**
     * 创建企业微信通知
     */
    @Override
    public Long create(Long schoolId, Long userId, EnterpriseWechatNoticeSaveReqModel reqModel) {
        // 构建实体对象
        EnterpriseWechatNoticeEntity entity = BeanConvertUtil.convert(reqModel, EnterpriseWechatNoticeEntity.class);
        entity.setSendTime(reqModel.getSendTime() == null || reqModel.getSendTime().trim().isEmpty() ?
                null : LocalDateTime.parse(reqModel.getSendTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        entity.setSchoolId(schoolId);
        entity.setCreatorId(userId);
        // 保存数据
        this.save(entity);
        if (CollectionUtils.isNotEmpty(reqModel.getFileIds())) {
            sysFileRelevanceService.saveBatch(reqModel.getFileIds().stream().map(fileId -> {
                SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                fileRelevanceEntity.setFileId(fileId);
                fileRelevanceEntity.setType(FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());
                fileRelevanceEntity.setBusinessId(entity.getId());
                fileRelevanceEntity.setSchoolId(schoolId);
                return fileRelevanceEntity;
            }).collect(Collectors.toList()));
        }
        if (reqModel.getSendTime() == null) {
            //异步发送通知
            noticeService.sendNotice(entity);
        }
        return entity.getId();
    }

    /**
     * 修改企业微信通知
     */
    @Override
    public void update(Long id, EnterpriseWechatNoticeUpdateReqModel reqModel) {
        EnterpriseWechatNoticeEntity entity = this.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(reqModel, entity);
            this.updateById(entity);
            //处理照片文件
            QueryWrapper<SysFileRelevanceEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysFileRelevanceEntity::getBusinessId, id)
                    .eq(SysFileRelevanceEntity::getSchoolId, entity.getSchoolId())
                    .eq(SysFileRelevanceEntity::getType, FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());
            List<SysFileRelevanceEntity> fileRelevanceEntities = sysFileRelevanceService.list(queryWrapper);
            // 获取数据库中已存在的文件ID列表
            List<Long> existingFileIds = fileRelevanceEntities.stream().map(SysFileRelevanceEntity::getFileId).collect(Collectors.toList());
            // 获取请求中的文件ID列表
            List<Long> requestFileIds = reqModel.getFileIds();
            if (CollectionUtils.isNotEmpty(requestFileIds)) {
                // 找出需要新增的文件ID（在请求中但不在数据库中）
                List<Long> fileIdsToAdd = requestFileIds.stream()
                        .filter(fileId -> !existingFileIds.contains(fileId))
                        .collect(Collectors.toList());

                // 找出需要删除的文件ID（在数据库中但不在请求中）
                List<Long> fileIdsToDelete = existingFileIds.stream()
                        .filter(fileId -> !requestFileIds.contains(fileId))
                        .collect(Collectors.toList());

                // 新增文件关联
                if (CollectionUtils.isNotEmpty(fileIdsToAdd)) {
                    List<SysFileRelevanceEntity> toAdd = fileIdsToAdd.stream().map(fileId -> {
                        SysFileRelevanceEntity fileRelevanceEntity = new SysFileRelevanceEntity();
                        fileRelevanceEntity.setFileId(fileId);
                        fileRelevanceEntity.setType(FileRelevanceTypeEnum.ENTERPRISE_WECHAT_NOTICE.getType());
                        fileRelevanceEntity.setBusinessId(id);
                        fileRelevanceEntity.setSchoolId(entity.getSchoolId());
                        return fileRelevanceEntity;
                    }).collect(Collectors.toList());
                    sysFileRelevanceService.saveBatch(toAdd);
                }

                // 删除文件关联
                if (CollectionUtils.isNotEmpty(fileIdsToDelete)) {
                    sysFileRelevanceService.removeByIds(fileIdsToDelete);
                }
            } else {
                // 如果请求中没有文件ID，删除所有已存在的文件关联
                if (CollectionUtils.isNotEmpty(fileRelevanceEntities)) {
                    sysFileRelevanceService.removeByIds(existingFileIds);
                }
            }
        }
    }

    /**
     * 删除企业微信通知
     */
    @Override
    public Boolean delete(Long id) {
        EnterpriseWechatNoticeEntity entity = this.getById(id);
        if (entity != null) {
            return this.removeById(id);
        }
        return false;
    }
}