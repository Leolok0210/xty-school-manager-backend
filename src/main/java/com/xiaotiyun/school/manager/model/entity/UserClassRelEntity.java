package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_class_rel")
public class UserClassRelEntity extends BaseEntity {
    /**
     * 学校ID
     */
    private Long schoolId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 关联类型，1-学部，2-级组，3-班级
     */
    private Integer type;

    /**
     * 关联ID
     */
    private Long relId;
}
