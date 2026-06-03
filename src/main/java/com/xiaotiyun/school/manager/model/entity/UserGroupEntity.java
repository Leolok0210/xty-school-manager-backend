package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user_group")
public class UserGroupEntity extends BaseEntity {
    /**
     * 用户组名称
     */
    private String name;
    
    /**
     * 用户组编码
     */
    private String code;
    
    /**
     * 学校ID,为0表示系统预设用户组
     */
    private Long schoolId;
    
    /**
     * 备注
     */
    private String remark;
} 