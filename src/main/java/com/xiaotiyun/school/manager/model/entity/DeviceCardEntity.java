package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_card_student")
public class DeviceCardEntity extends BaseEntity {
    /** 卡片ID */
    private String cardId;
    /** 学生学号 */
    private String studentId;
    /** 学生姓名 */
    private String name;
    /** 设备序列号 */
    private String deviceSn;
}
