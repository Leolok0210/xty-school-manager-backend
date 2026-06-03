package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.GradeGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.model.req.GradeGroupQueryReqModel;
import com.xiaotiyun.school.manager.model.res.GradeGroupDetailResModel;

import java.util.List;
import java.util.Map;

/**
* @author Akame
* @description 针对表【grade_group( 级组表)】的数据库操作Service
* @createDate 2025-02-11 16:47:24
*/
public interface GradeGroupService extends IService<GradeGroup> {

    /**
     * 获取级组列表
     * @param reqModel 查询请求信息
     * @return 分页结果
     */
    PageInfo<GradeGroupDetailResModel> getGradeGroupList(GradeGroupQueryReqModel reqModel);

    List<GradeGroup> getGradeAllGroupList(GradeGroupQueryReqModel reqModel);

    GradeGroup getGradeGroupByName(String gradeGroup, Long schoolId);

    //批量保存级组
    void createGradeGroups(List<GradeGroup> gradeGroups);

    Map<Long, String> getNamesByIds(List<Long> ids);

}