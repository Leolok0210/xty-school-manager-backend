package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentUsuallyTypeEntity;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeReqModel;
import com.xiaotiyun.school.manager.model.req.StudentUsuallyTypeSaveReqModel;
import com.xiaotiyun.school.manager.model.res.StudentUsuallyTypeResModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 平时成绩类型Service接口
 */
public interface StudentUsuallyTypeService extends IService<StudentUsuallyTypeEntity> {

    /**
     * 分页查询平时成绩类型列表
     *
     * @param reqModel 查询条件
     * @param schoolId 学校ID
     * @return 分页结果
     */
    PageInfo<StudentUsuallyTypeResModel> pageList(StudentUsuallyTypeReqModel reqModel, Long schoolId);

    /**
     * 新增和修改平时成绩类型
     *
     * @param reqModels 新增或修改信息
     * @param schoolId 学校ID
     * @return 是否成功
     */
    Result<Boolean> addOrUpdate(List<StudentUsuallyTypeSaveReqModel> reqModels, Long schoolId);

    Result<Boolean> delete(List<Long> ids, Long schoolId);

    /**
     * 检查类型名称是否存在
     * @param typeName 类型名称
     * @param schoolId 学校ID
     * @param id 主键ID（修改时传入）
     * @return 是否存在
     */
    boolean checkTypeNameExists(String typeName, Long schoolId, Long id);
}
