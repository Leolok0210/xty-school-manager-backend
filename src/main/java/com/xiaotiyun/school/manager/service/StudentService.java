package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.basic.common.Result;
import com.xiaotiyun.school.manager.model.entity.StudentEntity;
import com.xiaotiyun.school.manager.model.req.*;
import com.xiaotiyun.school.manager.model.res.StudentListResModel;
import com.xiaotiyun.school.manager.model.res.StudentPageResModel;
import com.xiaotiyun.school.manager.model.res.StudentResModel;
import com.xiaotiyun.school.manager.model.res.StudentScorePageResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentService extends IService<StudentEntity> {

    /**
     * 分页查询学生列表
     */
    PageInfo<StudentPageResModel> page(StudentPageReqModel reqModel);

    /**
     * 新增学生
     */
    Long save(StudentSaveReqModel reqModel);

    /**
     * 修改学生
     */
    void update(Long id, StudentSaveReqModel reqModel);

    /**
     * 获取学生
     */
    StudentResModel info(Long id);

    StudentResModel getStudentById(Long id);

    /**
     * 删除学生
     */
    void delete(Long id);

    /**
     * 导入学生
     */
    Long importStudent(String schoolYear, Long schoolId, MultipartFile file);

    /**
     * 导出学生
     */
    String exportStudent(Long userId, StudentPageExportReqModel reqModel);

    /**
     * 导出学生表头查询
     */
    String exportStudentHeader(Long schoolId, Long userId);

    /**
     * 包括升班的
     * @param classId
     * @return
     */
    List<StudentListResModel> listByClassId(Long classId);

    StudentResModel getStudentIdByNameAndSchoolId(String studentName, Long schoolId);

    /**
     * 单张照片上传
     *
     * @param file
     * @param schoolId
     * @param studentId
     */
    void uploadImage(MultipartFile file, Long schoolId, Long studentId);

    /**
     * 批量照片导入
     *
     * @param resModel
     * @rturn
     */
    Long batchImageUpload(StudentImageBatchUploadReqModel resModel);

    void updateStudentByHealthDeclare(StudentHealthDeclareAddReqModel reqModel);

    /**
     * 成绩
     *
     * @param resModel
     * @rturn
     */
    List<StudentScorePageResModel> score(StudentScoreReqModel resModel);

    /**
     * 下载学生相片
     *
     * @param resModel
     * @rturn
     */
    String downloadImage(Long schoolId, StudentImageDownloadReqModel resModel);


    //批量修改classId
    void updateClassId(List<Long> ids, Long classId);

    void updateStudentStatusByClassId(Long classId);

    /**
     * 查询未完成的批量上传任务
     */
    void queryUntreatedStudentImportTask();

    /**
     * 处理批量上传任务
     */
    void handleStudentImportBatchUpload();

    List<StudentEntity> getStudentListByClassId(Long classId);

    /**
     * 调用python服务，生成学校学生照片pdf
     * @param reqModel
     * @return
     */
    Result<String> getStudentImagePdf(StudentImagePDFReqModel reqModel);

    /**
     * 根据学生编号列表获取学生信息Map
     * @param schoolId 学校ID
     * @param studentNos 学生编号列表
     * @return 学生信息Map，key为学生编号，value为学生实体
     */
    Map<String, StudentEntity> getStudentMapByStudentNos(Long schoolId, List<String> studentNos);
}