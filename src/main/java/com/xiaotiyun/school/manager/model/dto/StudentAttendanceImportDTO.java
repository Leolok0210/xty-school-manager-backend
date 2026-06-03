package com.xiaotiyun.school.manager.model.dto;

import com.xiaotiyun.school.manager.basic.enums.StudentAttendanceImportTypeEnum;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class StudentAttendanceImportDTO {
    /**
     * 学生编号
     */
    private String studentNo;
    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 类型
     */
    private StudentAttendanceImportTypeEnum type;
    /**
     * 日期
     */
    private LocalDate attendanceDate;
    /**
     * 时间
     */
    private LocalTime attendanceTime;
}
