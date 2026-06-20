package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_attendance")
public class DeviceAttendanceEntity extends BaseEntity {
    /** 学生学号 */
    private String studentId;
    /** 学生姓名 */
    private String name;
    /** 打卡时间 (毫秒时间戳) */
    private Long attendanceTime;
    /** 打卡状态 (已打卡/遲到/早退等) */
    private String status;
    /** 班级ID */
    private Long classId;
    /** 设备序列号 */
    private String deviceSn;
}
