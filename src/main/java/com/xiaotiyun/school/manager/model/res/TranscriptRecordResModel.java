package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "学年成绩单响应类")
public class TranscriptRecordResModel {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "学年")
    private String schoolYear;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty(value = "级组ID", example = "1")
    private String classGroupId;

    @ApiModelProperty(value = "级组名称", example = "高一")
    private String classGroupName;

    @ApiModelProperty(value = "班级ID", example = "1")
    private String classId;

    @ApiModelProperty(value = "班级名称", example = "高一(1)班")
    private String className;

    @ApiModelProperty(value = "创建人ID")
    private Long registrantId;

    @ApiModelProperty(value = "创建人")
    private String registrant;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "处理时间")
    private String handleTime;

    @ApiModelProperty(value = "状态(0:处理中；1：处理成功)")
    private Integer status;

    @ApiModelProperty(value = "压缩包地址")
    private String zipUrl;
}