package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClassroomPageResModel {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("教室名称")
    private String name;

    @ApiModelProperty("教室类型ID")
    private Long typeId;

    @ApiModelProperty("教室类型名称(系统预设类型为多语言json字符串,类似【{\"zh-MO\": \"基礎管理\", \"en-US\": \"Basic\", \"pt-PT\": \"Básica\"}】)")
    private String typeName;

    @ApiModelProperty("教室类型是否系统预设")
    private Boolean isSystem;

    @ApiModelProperty("所在楼名")
    private String building;

    @ApiModelProperty("所在楼层")
    private String floor;
}