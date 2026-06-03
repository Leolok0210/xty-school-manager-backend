package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentMedicalRecordEntity;
import com.xiaotiyun.school.manager.model.req.MedicalRecordReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordAddReqModel;
import com.xiaotiyun.school.manager.model.req.StudentMedicalRecordUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.MedicalRecordResModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

public interface StudentMedicalRecordService extends IService<StudentMedicalRecordEntity> {
    /**
     * 根据请求参数查询学生医护保健记录列表
     *
     * @param reqModel 请求参数对象
     * @return 包含学生医护保健记录列表的结果对象
     */
    Result<PageInfo<MedicalRecordResModel>> listStudentMedicalRecords(MedicalRecordReqModel reqModel);

    /**
     * 添加新的学生医护保健记录
     *
     * @param entity 学生医护保健记录实体对象
     * @return 操作结果对象
     */
    Result<String> addStudentMedicalRecord(StudentMedicalRecordAddReqModel entity, Long schoolId);

    /**
     * 更新学生医护保健记录
     *
     * @param entity 学生医护保健记录实体对象
     * @return 操作结果对象
     */
    Result<String> updateStudentMedicalRecord(StudentMedicalRecordUpdateReqModel entity);

    /**
     * 删除指定ID的学生医护保健记录
     *
     * @param id 学生医护保健记录ID
     * @return 操作结果对象
     */
    Result<String> deleteStudentMedicalRecord(Long id);

    ResponseEntity<byte[]> exportMedicalRecords(MedicalRecordReqModel reqModel) throws UnsupportedEncodingException;

    Long importMedicalRecords(MultipartFile file, Long schoolId, String schoolYear);
}
