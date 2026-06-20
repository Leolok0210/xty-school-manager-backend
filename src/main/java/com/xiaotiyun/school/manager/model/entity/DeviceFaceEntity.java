package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_face")
public class DeviceFaceEntity extends BaseEntity {
    /** 学生学号 */
    private String studentId;
    /** 学生姓名 */
    private String name;
    /** 设备序列号 */
    private String deviceSn;
    /** 人脸注册状态 */
    private String status;
}
