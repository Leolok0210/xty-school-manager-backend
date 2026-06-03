package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.DeptEntity;
import com.xiaotiyun.school.manager.model.req.DeptReqModel;
import com.xiaotiyun.school.manager.model.res.DeptResModel;
import com.xiaotiyun.school.manager.model.res.UserSchoolRelResModel;

import java.util.List;

/**
 * 部门Service接口
 */
public interface DeptService extends IService<DeptEntity> {
    /**
     * 获取部门列表
     * @param deptReqModel 查询条件
     * @return 部门列表
     */
//    List<DeptResModel> listDepts(DeptReqModel deptReqModel);

    /**
     * 获取部门详情
     * @param id 部门ID
     * @return 部门详情
     */
    DeptResModel getDeptById(Long id);

    /**
     * 新增部门
     *
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    Result<Boolean> saveDept(DeptReqModel deptReqModel);

    /**
     * 更新部门
     *
     * @param deptReqModel 部门信息
     * @return 操作结果
     */
    Result<Boolean> updateDept(DeptReqModel deptReqModel);

    /**
     * 删除部门
     *
     * @param id 部门ID
     * @return 操作结果
     */
    Result<Boolean> deleteDept(Long id);

    /**
     * 获取部门树
     * @param schoolId 学校ID
     * @return 部门树结构
     */
    List<DeptResModel> getDeptTree(Long schoolId, boolean withUser);

    List<UserSchoolRelResModel> getDeptUsers(Long schoolId, Long deptId);
}