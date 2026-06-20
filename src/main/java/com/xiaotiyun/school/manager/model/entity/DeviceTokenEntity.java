package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device_token")
public class DeviceTokenEntity extends BaseEntity {
    /** 设备序列号 */
    private String deviceSn;
    /** 设备名称 */
    private String deviceName;
    /** 认证令牌 */
    private String token;
    /** 令牌过期时间 */
    private LocalDateTime expireTime;
}
