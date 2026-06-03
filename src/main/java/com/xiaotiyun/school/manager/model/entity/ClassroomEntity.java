package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("classroom")
@ApiModel(value = "教室实体")
public class ClassroomEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "教室名称", required = true)
    private String name;

    @ApiModelProperty(value = "教室类型ID", required = true)
    private Long typeId;

    @ApiModelProperty("所在楼名")
    private String building;

    @ApiModelProperty("所在楼层")
    private String floor;
} 