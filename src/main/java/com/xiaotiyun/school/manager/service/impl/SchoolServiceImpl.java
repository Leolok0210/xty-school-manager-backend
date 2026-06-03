package com.xiaotiyun.school.manager.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.NumberConstant;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import com.xiaotiyun.school.manager.dao.SchoolDao;
import com.xiaotiyun.school.manager.dao.SchoolMenuDao;
import com.xiaotiyun.school.manager.dao.UserSchoolRelDao;
import com.xiaotiyun.school.manager.model.entity.*;
import com.xiaotiyun.school.manager.model.req.SchoolAddReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolMenuBatchUpdateReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailResModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailStudentResModel;
import com.xiaotiyun.school.manager.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolDao, SchoolEntity> implements SchoolService {

    @Resource
    private SchoolMenuDao schoolMenuDao;
    @Resource
    private StudentService studentService;
    @Resource
    private UserSchoolRelDao userSchoolRelDao;
    @Resource
    private SysClassService sysClassService;
    @Resource
    private ActProcessTemplateService actProcessTemplateService;
    @Resource
    private WechatMiniprogramChannelService wechatMiniprogramChannelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addSchool(SchoolAddReqModel reqModel) {
        // 1. 校验学校名称是否重复
        if (isSchoolNameExists(reqModel.getName(), null)) {
            throw new BusinessException(LanguageConstants.SCHOOL_NAME_EXISTS);
        }

        // 2. 校验学校编号是否重复
        String code = reqModel.getCode();
        if (StringUtils.isBlank(code)) {
            // 生成20位随机编号
            code = RandomUtil.randomString(20);
            while (isSchoolCodeExists(code, null)) {
                code = RandomUtil.randomString(20);
            }
        } else if (isSchoolCodeExists(code, null)) {
            throw new BusinessException(LanguageConstants.SCHOOL_CODE_EXISTS);
        }

        // 3. 保存学校信息
        SchoolEntity school = new SchoolEntity();
        BeanUtils.copyProperties(reqModel, school);
        school.setCode(code);
        school.setSchoolType(reqModel.getSchoolTypes() != null ? 
            reqModel.getSchoolTypes().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) : 
            null);
        save(school);

        // 4. 保存学校菜单关联
        List<SchoolMenuEntity> menuList = reqModel.getMenuIds().stream().map(menuId -> {
            SchoolMenuEntity menu = new SchoolMenuEntity();
            menu.setSchoolId(school.getId());
            menu.setMenuId(menuId);
            return menu;
        }).collect(Collectors.toList());
        schoolMenuDao.insertBatch(menuList);
        //初始化学校审批模版
        actProcessTemplateService.initSchoolTemplates(school.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSchool(Long id, SchoolAddReqModel reqModel) {
        // 1. 校验学校名称是否重复
        if (isSchoolNameExists(reqModel.getName(), id)) {
            throw new BusinessException(LanguageConstants.SCHOOL_NAME_EXISTS);
        }

        // 2. 校验学校编号是否重复
        if (StringUtils.isNotBlank(reqModel.getCode()) && isSchoolCodeExists(reqModel.getCode(), id)) {
            throw new BusinessException(LanguageConstants.SCHOOL_CODE_EXISTS);
        }

        // 3. 更新学校信息
        SchoolEntity school = new SchoolEntity();
        BeanUtils.copyProperties(reqModel, school);
        school.setId(id);
        school.setSchoolType(reqModel.getSchoolTypes() != null ? 
            reqModel.getSchoolTypes().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) : 
            null);
        school.setExpireTime(reqModel.getExpireTime());
        updateById(school);

        // 4. 更新学校菜单关联
        schoolMenuDao.delete(new LambdaQueryWrapper<SchoolMenuEntity>()
                .eq(SchoolMenuEntity::getSchoolId, id));
        List<SchoolMenuEntity> menuList = reqModel.getMenuIds().stream().map(menuId -> {
            SchoolMenuEntity menu = new SchoolMenuEntity();
            menu.setSchoolId(id);
            menu.setMenuId(menuId);
            return menu;
        }).collect(Collectors.toList());
        schoolMenuDao.insertBatch(menuList);
        //初始化学校审批模版
        actProcessTemplateService.initSchoolTemplates(school.getId());
    }

    @Override
    public SchoolDetailResModel getSchoolDetail(Long id) {
        // 1. 获取学校信息
        SchoolEntity school = getById(id);
        if (school == null) {
            return null;
        }

        // 2. 获取学校菜单关联
        List<SchoolMenuEntity> menuList = schoolMenuDao.selectList(new LambdaQueryWrapper<SchoolMenuEntity>()
                .eq(SchoolMenuEntity::getSchoolId, id));

        // 3. 组装返回数据
        SchoolDetailResModel resModel = new SchoolDetailResModel();
        BeanUtils.copyProperties(school, resModel);
        resModel.setSchoolTypes(StringUtils.isNotBlank(school.getSchoolType()) ? 
            Arrays.stream(school.getSchoolType().split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList()) : 
            null);
        resModel.setMenuIds(menuList.stream().map(SchoolMenuEntity::getMenuId).collect(Collectors.toList()));
        resModel.setStatus(school.getExpireTime() == null || school.getExpireTime().isAfter(LocalDateTime.now()));
        return resModel;
    }

    @Override
    public PageInfo<SchoolDetailResModel> getSchoolList(SchoolQueryReqModel reqModel) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SchoolEntity> wrapper = new LambdaQueryWrapper<SchoolEntity>()
                .like(StringUtils.isNotBlank(reqModel.getName()), SchoolEntity::getName, reqModel.getName())
                .like(StringUtils.isNotBlank(reqModel.getSchoolType()), SchoolEntity::getSchoolType, reqModel.getSchoolType())
                .like(StringUtils.isNotBlank(reqModel.getProvince()), SchoolEntity::getProvince, reqModel.getProvince())
                .like(StringUtils.isNotBlank(reqModel.getCity()), SchoolEntity::getCity, reqModel.getCity())
                .like(StringUtils.isNotBlank(reqModel.getDistrict()), SchoolEntity::getDistrict, reqModel.getDistrict())
                .ge(reqModel.getExpireTimeStart() != null, SchoolEntity::getExpireTime, reqModel.getExpireTimeStart())
                .le(reqModel.getExpireTimeEnd() != null, SchoolEntity::getExpireTime, reqModel.getExpireTimeEnd());
        if (reqModel.getStatus() != null) {
            if (reqModel.getStatus()) {
                // 未过期
                wrapper.and(w -> w.isNull(SchoolEntity::getExpireTime)
                        .or()
                        .gt(SchoolEntity::getExpireTime, LocalDateTime.now()));
            } else{
                // 已过期
                wrapper.lt(SchoolEntity::getExpireTime, LocalDateTime.now());
            }
        }
        wrapper.orderByDesc(SchoolEntity::getCreateTime);

        // 2. 开启分页并查询数据
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<SchoolEntity> list = baseMapper.selectList(wrapper);
        PageInfo<SchoolEntity> pageInfo = new PageInfo<>(list);

        // 3. 转换返回结果
        List<SchoolDetailResModel> resList = list.stream().map(school -> {
            SchoolDetailResModel resModel = new SchoolDetailResModel();
            BeanUtils.copyProperties(school, resModel);
            resModel.setSchoolTypes(StringUtils.isNotBlank(school.getSchoolType()) ? 
                Arrays.stream(school.getSchoolType().split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList()) : 
                null);
            resModel.setStatus(school.getExpireTime() == null || school.getExpireTime().isAfter(LocalDateTime.now()));
            return resModel;
        }).collect(Collectors.toList());

        // 4. 创建新的PageInfo并复制分页信息
        PageInfo<SchoolDetailResModel> result = new PageInfo<>(resList);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        
        return result;
    }

    @Override
    public PageInfo<SchoolDetailStudentResModel> getSchoolListByStudent(SchoolQueryReqModel reqModel) {
        // 1. 构建查询条件
        LambdaQueryWrapper<SchoolEntity> wrapper = new LambdaQueryWrapper<SchoolEntity>()
                .like(StringUtils.isNotBlank(reqModel.getName()), SchoolEntity::getName, reqModel.getName())
                .like(StringUtils.isNotBlank(reqModel.getSchoolType()), SchoolEntity::getSchoolType, reqModel.getSchoolType())
                .like(StringUtils.isNotBlank(reqModel.getProvince()), SchoolEntity::getProvince, reqModel.getProvince())
                .like(StringUtils.isNotBlank(reqModel.getCity()), SchoolEntity::getCity, reqModel.getCity())
                .like(StringUtils.isNotBlank(reqModel.getDistrict()), SchoolEntity::getDistrict, reqModel.getDistrict())
                .ge(reqModel.getExpireTimeStart() != null, SchoolEntity::getExpireTime, reqModel.getExpireTimeStart())
                .le(reqModel.getExpireTimeEnd() != null, SchoolEntity::getExpireTime, reqModel.getExpireTimeEnd());
        if (reqModel.getStatus() != null) {
            if (reqModel.getStatus()) {
                // 未过期
                wrapper.and(w -> w.isNull(SchoolEntity::getExpireTime)
                        .or()
                        .gt(SchoolEntity::getExpireTime, LocalDateTime.now()));
            } else{
                // 已过期
                wrapper.lt(SchoolEntity::getExpireTime, LocalDateTime.now());
            }
        }
        wrapper.orderByDesc(SchoolEntity::getCreateTime);

        // 2. 开启分页并查询数据
        PageHelper.startPage(reqModel.getPageNum(), reqModel.getPageSize());
        List<SchoolEntity> list = baseMapper.selectList(wrapper);
        PageInfo<SchoolEntity> pageInfo = new PageInfo<>(list);

        // 3. 转换返回结果
        List<SchoolDetailStudentResModel> resList = list.stream().map(school -> {
            SchoolDetailStudentResModel resModel = new SchoolDetailStudentResModel();
            BeanUtils.copyProperties(school, resModel);
            resModel.setSchoolTypes(StringUtils.isNotBlank(school.getSchoolType()) ?
                    Arrays.stream(school.getSchoolType().split(","))
                            .map(Integer::valueOf)
                            .collect(Collectors.toList()) :
                    null);
            resModel.setStatus(school.getExpireTime() == null || school.getExpireTime().isAfter(LocalDateTime.now()));
            return resModel;
        }).collect(Collectors.toList());

        // 拼接渠道信息
        if (!CollectionUtils.isEmpty(resList)) {
            List<Long> channelIds = resList.stream().map(SchoolDetailStudentResModel::getChannelId).filter(Objects::nonNull).collect(Collectors.toList());
            List<WechatMiniprogramChannelEntity> channelEntities = wechatMiniprogramChannelService.list(new LambdaQueryWrapper<WechatMiniprogramChannelEntity>()
                    .in(WechatMiniprogramChannelEntity::getId, channelIds));
            if (!CollectionUtils.isEmpty(channelEntities)) {
                Map<Long, WechatMiniprogramChannelEntity> channelMap = channelEntities.stream().collect(Collectors.toMap(WechatMiniprogramChannelEntity::getId, Function.identity()));
                resList.forEach(school -> {
                    WechatMiniprogramChannelEntity channel = channelMap.get(school.getChannelId());
                    if (channel != null) {
                        school.setChannelName(channel.getChannelName());
                        school.setChannelUrl(channel.getChannelUrl());
                        school.setChannelPublic(channel.getChannelPublic());
                    }
                });
            }
        }

        // 4. 创建新的PageInfo并复制分页信息
        PageInfo<SchoolDetailStudentResModel> result = new PageInfo<>(resList);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSchoolMenu(SchoolMenuBatchUpdateReqModel reqModel) {
        // 1. 获取学校已开通的菜单
        List<SchoolMenuEntity> existMenuList = schoolMenuDao.selectList(new LambdaQueryWrapper<SchoolMenuEntity>()
                .in(SchoolMenuEntity::getSchoolId, reqModel.getSchoolIds())
                .in(SchoolMenuEntity::getMenuId, reqModel.getMenuIds()));

        // 2. 过滤出未开通的菜单
        List<SchoolMenuEntity> insertList = reqModel.getSchoolIds().stream()
                .flatMap(schoolId -> reqModel.getMenuIds().stream()
                        .filter(menuId -> existMenuList.stream()
                                .noneMatch(menu -> menu.getSchoolId().equals(schoolId) && menu.getMenuId().equals(menuId)))
                        .map(menuId -> {
                            SchoolMenuEntity menu = new SchoolMenuEntity();
                            menu.setSchoolId(schoolId);
                            menu.setMenuId(menuId);
                            return menu;
                        }))
                .collect(Collectors.toList());

        // 3. 批量插入未开通的菜单
        if (!insertList.isEmpty()) {
            schoolMenuDao.insertBatch(insertList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchool(Long id) {
        // 1. 检查学校是否存在
        SchoolEntity school = this.getById(id);
        if (school == null) {
            throw new BusinessException(LanguageConstants.SCHOOL_NOT_EXISTS);
        }
        
        // 2. 检查学校是否可以删除（例如：是否还有关联的学生、教师等）
        checkSchoolCanDelete(id);
        
        // 3. 执行逻辑删除
        this.removeById(id);
    }
    
    /**
     * 检查学校是否可以删除
     * @param schoolId 学校ID
     */
    private void checkSchoolCanDelete(Long schoolId) {
        // 有关联的学生，班级，用户，则不可以删除
        if (studentService.count(new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getSchoolId, schoolId)) > 0) {
            throw new BusinessException(LanguageConstants.SCHOOL_HAS_STUDENT);
        }
        if (sysClassService.count(new LambdaQueryWrapper<SysClass>().eq(SysClass::getSchoolId, schoolId)) > 0) {
            throw new BusinessException(LanguageConstants.SCHOOL_HAS_STUDENT);
        }
        if (userSchoolRelDao.selectCount(new LambdaQueryWrapper<UserSchoolRelEntity>()
                .eq(UserSchoolRelEntity::getSchoolId, schoolId)) > 0) {
            throw new BusinessException(LanguageConstants.SCHOOL_HAS_STUDENT);
        }
    }

    /**
     * 校验学校名称是否存在
     */
    private boolean isSchoolNameExists(String name, Long excludeId) {
        LambdaQueryWrapper<SchoolEntity> wrapper = new LambdaQueryWrapper<SchoolEntity>()
                .eq(SchoolEntity::getName, name);
        if (excludeId != null) {
            wrapper.ne(SchoolEntity::getId, excludeId);
        }
        return count(wrapper) > 0;
    }

    /**
     * 校验学校编号是否存在
     */
    private boolean isSchoolCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<SchoolEntity> wrapper = new LambdaQueryWrapper<SchoolEntity>()
                .eq(SchoolEntity::getCode, code);
        if (excludeId != null) {
            wrapper.ne(SchoolEntity::getId, excludeId);
        }
        return count(wrapper) > 0;
    }
}