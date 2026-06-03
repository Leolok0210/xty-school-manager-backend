package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.MenuEntity;
import com.xiaotiyun.school.manager.model.req.MenuPageReqModel;
import com.xiaotiyun.school.manager.model.req.MenuSaveReqModel;
import com.xiaotiyun.school.manager.model.res.MenuResModel;
import com.xiaotiyun.school.manager.model.res.MenuTreeResModel;

import java.util.List;

public interface MenuService extends IService<MenuEntity> {
    
    void save(MenuSaveReqModel reqModel);
    
    void update(Long id, MenuSaveReqModel reqModel);
    
    void delete(Long id);

    /**
     * 查询菜单列表
     * @param reqModel 查询参数
     * @return 菜单树形列表
     */
    List<MenuResModel> list(MenuPageReqModel reqModel);

    /**
     * 获取指定学校的菜单列表
     * @param schoolId 学校ID
     * @return 菜单列表
     */
    List<MenuResModel> getSchoolMenuList(Long schoolId);

    List<MenuResModel> buildMenuTree(List<MenuEntity> menuList);
}