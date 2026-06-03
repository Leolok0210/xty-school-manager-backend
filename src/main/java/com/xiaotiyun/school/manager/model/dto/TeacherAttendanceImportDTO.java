package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TeacherAttendanceImportDTO {
    /**
     * 用户编号
     */
    private String userNumber;
    /**
     * 教师姓名
     */
    private String teacherName;
    /**
     * 卡号
     */
    private String cardNumber;
    /**
     * 日期
     */
    private LocalDate attendanceDate;
    /**
     * 时间
     */
    private LocalTime attendanceTime;
}
