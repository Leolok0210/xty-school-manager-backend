package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("student_attendance")
public class StudentAttendanceEntity extends BaseEntity {
    /**
     * 学校id
     */
    private Long schoolId;
    /**
     * 学年
     */
    private String schoolYear;
    /**
     * 学生ID
     */
    private Long studentId;
    /**
     * 班级id
     */
    private Long classId;
    /**
     * 日期
     */
    private LocalDate attendanceDate;
    /**
     * 上午入校时间
     */
    private LocalTime morningInTime;
    /**
     * 上午离校时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalTime morningOutTime;
    /**
     * 下午入校时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalTime afternoonInTime;
    /**
     * 下午离校时间
     */
    private LocalTime afternoonOutTime;
    /**
     * 出勤状态（0.正常;1.迟到;2.早退;3.缺卡;4.数据异常）
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
}