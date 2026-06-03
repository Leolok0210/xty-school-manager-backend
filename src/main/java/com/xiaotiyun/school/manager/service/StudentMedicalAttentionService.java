package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentMedicalAttentionEntity;
import com.xiaotiyun.school.manager.model.req.StudentHealthDeclareAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalAttentionUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentMedicalAttentionResModel;

public interface StudentMedicalAttentionService extends IService<StudentMedicalAttentionEntity> {
    /**
     * 根据请求参数查询学生医护注意事项列表
     *
     * @param reqModel 请求参数对象
     * @return 包含学生医护注意事项列表的结果对象
     */
    Result<StudentMedicalAttentionResModel> listStudentMedicalAttentions(StudentMedicalAttentionReqModel reqModel);


    /**
     * 更新学生医护注意事项
     *
     * @param entity 学生医护注意事项实体对象
     * @param schoolId 学校ID
     * @return 操作结果对象
     */
    Result<String> updateStudentMedicalAttention(StudentMedicalAttentionUpdateReqModel entity, Long schoolId);

    void updateByHealthDeclare(StudentHealthDeclareAddReqModel reqModel);
}