package com.xiaotiyun.school.manager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.xiaotiyun.school.manager.model.entity.StudentAttendanceEntity;
import com.xiaotiyun.school.manager.model.req.StudentAttendancePageReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceReportReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceStatisticsReqModel;
import com.xiaotiyun.school.manager.model.req.StudentAttendanceUpdateReqModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendancePageResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceReportResModel;
import com.xiaotiyun.school.manager.model.res.StudentAttendanceStatisticsResModel;
import com.xiaotiyun.school.manager.model.res.StudentLateCountResModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentAttendanceService extends IService<StudentAttendanceEntity> {
    void update(Long id, StudentAttendanceUpdateReqModel reqModel);

    PageInfo<StudentAttendancePageResModel> page(StudentAttendancePageReqModel reqModel);

    void delete(Long id);

    /**
     * 导入记录
     */
    Long importRecord(Long schoolId, MultipartFile file);

    /**
     * 导出
     */
    String export(StudentAttendancePageReqModel reqModel);

    /**
     * 考勤统计
     */
    List<StudentAttendanceStatisticsResModel> statistics(StudentAttendanceStatisticsReqModel reqModel);

    /**
     * 查询学生迟到统计
     * 根据学生ID分组统计迟到次数
     *
     * @param schoolId   学校ID
     * @param classId    班级ID
     * @param studentIds 学生ID列表
     * @return 返回学生迟到统计列表
     */
    List<StudentLateCountResModel> getStudentLateCount(Long schoolId, Long classId, List<Long> studentIds);

    /**
     * 统计学生在当前学段的迟到次数
     *
     * @param classId 班级ID
     * @param periodId 学段ID
     * @return 学生ID和对应的迟到次数
     */
    Map<Long, Integer> countStudentLateDays(Long classId, Long periodId);

    /**
     * 学生出勤报表
     *
     * @param schoolId
     * @param reqModel
     * @return
     */
    List<StudentAttendanceReportResModel> report(Long schoolId, StudentAttendanceReportReqModel reqModel);

    /**
     * 学生出勤报表导出
     *
     * @param schoolId
     * @param reqModel
     * @return
     */
    String reportExport(Long schoolId, StudentAttendanceReportReqModel reqModel);



    /**
     * 统计学生在当前学段的迟到次数
     *
     * @param classId 班级ID
     * @param periodId 学段ID
     * @return 学生ID和对应的迟到次数
     */
    Map<Long, Integer> countFilterStudentLateDays(Long classId, Long periodId);


    List<StudentAttendanceEntity>  getFilterRecords(Map<Long, List<StudentAttendanceEntity>> listMap);
}