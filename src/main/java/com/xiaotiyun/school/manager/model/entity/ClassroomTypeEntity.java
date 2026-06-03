package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classroom_type")
@ApiModel(value = "教室类型实体")
public class ClassroomTypeEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "类型名称", required = true)
    private String name;

    @ApiModelProperty(value = "是否系统预设", required = true)
    private Boolean isSystem;
} 