package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.SchoolEntity;
import com.xiaotiyun.school.manager.model.req.SchoolAddReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolMenuBatchUpdateReqModel;
import com.xiaotiyun.school.manager.model.req.SchoolQueryReqModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailResModel;
import com.xiaotiyun.school.manager.model.res.SchoolDetailStudentResModel;

public interface SchoolService extends IService<SchoolEntity> {
    /**
     * 新增学校
     */
    void addSchool(SchoolAddReqModel reqModel);
    
    /**
     * 修改学校
     */
    void updateSchool(Long id, SchoolAddReqModel reqModel);
    
    /**
     * 查看学校详情
     */
    SchoolDetailResModel getSchoolDetail(Long id);
    
    /**
     * 查询学校列表
     */
    PageInfo<SchoolDetailResModel> getSchoolList(SchoolQueryReqModel reqModel);

    PageInfo<SchoolDetailStudentResModel> getSchoolListByStudent(SchoolQueryReqModel reqModel);

    /**
     * 批量开通学校菜单
     */
    void batchUpdateSchoolMenu(SchoolMenuBatchUpdateReqModel reqModel);

    /**
     * 删除学校
     * @param id 学校ID
     */
    void deleteSchool(Long id);
} 