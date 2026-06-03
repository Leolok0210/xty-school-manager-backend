package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("sys_file")
public class SysFileEntity extends BaseEntity {
    @TableField("name")
    private String name;

    @TableField("path")
    private String path;

    @TableField("suffix")
    private String suffix;

    @TableField("operator_id")
    private Long operatorId;
}