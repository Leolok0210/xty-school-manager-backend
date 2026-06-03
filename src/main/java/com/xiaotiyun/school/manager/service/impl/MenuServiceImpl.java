package com.xiaotiyun.school.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.constant.NumberConstant;
import com.xiaotiyun.school.manager.basic.exception.BusinessException;
import com.xiaotiyun.school.manager.basic.util.BeanConvertUtil;
import com.xiaotiyun.school.manager.dao.MenuMapper;
import com.xiaotiyun.school.manager.dao.SchoolMenuDao;
import com.xiaotiyun.school.manager.dao.UserGroupDao;
import com.xiaotiyun.school.manager.dao.UserGroupMenuDao;
import com.xiaotiyun.school.manager.model.entity.MenuEntity;
import com.xiaotiyun.school.manager.model.entity.SchoolMenuEntity;
import com.xiaotiyun.school.manager.model.entity.UserGroupEntity;
import com.xiaotiyun.school.manager.model.entity.UserGroupMenuEntity;
import com.xiaotiyun.school.manager.model.req.MenuPageReqModel;
import com.xiaotiyun.school.manager.model.req.MenuSaveReqModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;
import com.xiaotiyun.school.manager.model.res.MenuTreeResModel;
import com.xiaotiyun.school.manager.service.MenuService;
import com.xiaotiyun.school.manager.service.UserGroupMenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;

@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    @Resource
    UserGroupMenuService userGroupMenuService;
    @Resource
    SchoolMenuDao schoolMenuDao;

    @Override
    public List<MenuResModel> list(MenuPageReqModel reqModel) {
        log.info("查询菜单列表，参数：{}", reqModel);

        // 构建查询条件
        LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        wrapper.like(StringUtils.isNotBlank(reqModel.getMenuName()), MenuEntity::getMenuName, reqModel.getMenuName())
                .eq(reqModel.getType() != null, MenuEntity::getType, reqModel.getType())
                .eq(reqModel.getStatus() != null, MenuEntity::getStatus, reqModel.getStatus())
                .eq(MenuEntity::getDeleted, 0);

        // 按排序值升序，创建时间降序
        wrapper.orderByAsc(MenuEntity::getSort)
                .orderByDesc(MenuEntity::getCreateTime);

        // 查询所有菜单
        List<MenuEntity> allMenus = this.list(wrapper);

        // 转换并构建菜单树
        return buildMenuTree(allMenus);
    }


    /**
     * 构建菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    @Override
    public List<MenuResModel> buildMenuTree(List<MenuEntity> menuList) {
        // 转换为MenuResModel
        List<MenuResModel> menuModelList = BeanConvertUtil.convertList(menuList, MenuResModel.class);

        // 找出顶级菜单（parentId为0或null的）
        List<MenuResModel> topLevelMenus = menuModelList.stream()
                .filter(menu -> menu.getParentId() == null || menu.getParentId() == 0)
                .collect(Collectors.toList());

        // 如果没有明确的顶级菜单，则使用原来的逻辑作为备选
        if (topLevelMenus.isEmpty()) {
            topLevelMenus = menuModelList.stream()
                    .filter(menu -> !menuModelList.stream()
                            .map(MenuResModel::getId)
                            .collect(Collectors.toList())
                            .contains(menu.getParentId()))
                    .collect(Collectors.toList());
        }

        // 构建树形结构
        topLevelMenus.forEach(menu -> buildChildrenMenu(menu, menuModelList));

        return topLevelMenus;
    }

    /**
     * 递归构建子菜单
     *
     * @param parent   父菜单
     * @param allMenus 所有菜单列表
     */
    private void buildChildrenMenu(MenuResModel parent, List<MenuResModel> allMenus) {
        List<MenuResModel> children = allMenus.stream()
                .filter(menu -> parent.getId().equals(menu.getParentId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            children.forEach(child -> buildChildrenMenu(child, allMenus));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MenuSaveReqModel reqModel) {
        log.info("新增菜单，参数：{}", reqModel);
        // 校验上级菜单是否存在
        checkParentMenu(reqModel.getParentId());
        // 校验菜单名称是否重复
        checkMenuNameDuplicate(null, reqModel);

        MenuEntity entity = BeanConvertUtil.convert(reqModel, MenuEntity.class);
        this.save(entity);
        log.info("新增菜单成功，id：{}", entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, MenuSaveReqModel reqModel) {
        log.info("修改菜单，id：{}，参数：{}", id, reqModel);
        // 校验菜单是否存在
        MenuEntity entity = this.getById(id);
        if (entity == null) {
            throw new BusinessException(LanguageConstants.MENU_NOT_EXISTS);
        }
        // 校验上级菜单是否存在
        checkParentMenu(reqModel.getParentId());
        // 校验菜单名称是否重复
        checkMenuNameDuplicate(id, reqModel);

        BeanUtils.copyProperties(reqModel, entity);
        this.updateById(entity);
        log.info("修改菜单成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 1. 检查是否有子菜单
        boolean hasChildren = baseMapper.exists(new LambdaQueryWrapper<MenuEntity>()
                .eq(MenuEntity::getParentId, id));
        if (hasChildren) {
            throw new BusinessException(LanguageConstants.MENU_HAS_CHILDREN);
        }

        // 2. 逻辑删除菜单
        this.removeById(id);

        // 3. 逻辑删除用户组菜单关联
        LambdaQueryWrapper<UserGroupMenuEntity> wrapper = new LambdaQueryWrapper<UserGroupMenuEntity>()
                .eq(UserGroupMenuEntity::getMenuId, id);

        userGroupMenuService.remove(wrapper);
    }

    /**
     * 校验上级菜单是否存在
     *
     * @param parentId 上级菜单ID
     */
    private void checkParentMenu(Long parentId) {
        if (parentId == 0) {
            return;
        }
        MenuEntity parentMenu = this.getById(parentId);
        if (parentMenu == null) {
            throw new BusinessException(LanguageConstants.PARENT_MENU_NOT_EXISTS);
        }
        if (parentMenu.getType() != 1) {
            throw new BusinessException(LanguageConstants.PARENT_MENU_TYPE_ERROR);
        }
    }

    /**
     * 校验菜单名称是否重复
     *
     * @param id       菜单ID（修改时使用，新增时为null）
     * @param reqModel 菜单保存请求模型
     */
    private void checkMenuNameDuplicate(Long id, MenuSaveReqModel reqModel) {
        LambdaQueryWrapper<MenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MenuEntity::getMenuName, reqModel.getMenuName())
                .eq(MenuEntity::getParentId, reqModel.getParentId())
                .ne(id != null, MenuEntity::getId, id)
                .eq(MenuEntity::getDeleted, false);

        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(LanguageConstants.MENU_NAME_DUPLICATE);
        }
    }

    @Override
    public List<MenuResModel> getSchoolMenuList(Long schoolId) {
        // 校验schoolId
        if(schoolId == null){
            throw new BusinessException(LanguageConstants.SCHOOL_ID_REQUIRED);
        }

        // 查询学校菜单关联
        LambdaQueryWrapper<SchoolMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolMenuEntity::getSchoolId, schoolId);

        List<SchoolMenuEntity> list = schoolMenuDao.selectList(wrapper);
        // 查询菜单列表
        List<MenuEntity> menuList = this.list(new LambdaQueryWrapper<MenuEntity>()
                .in(MenuEntity::getId, list.stream().map(SchoolMenuEntity::getMenuId).collect(Collectors.toList())));

        // 转换为菜单树状列表
        return buildMenuTree(menuList);
    }
}