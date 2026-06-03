package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("transcript_record")
@ApiModel(description = "学年成绩单实体类")
public class TranscriptRecordEntity extends BaseEntity {

    @ApiModelProperty(value = "学校ID", required = true)
    @TableField("school_id")
    private Long schoolId;

    @ApiModelProperty(value = "学年", required = true)
    @TableField("school_year")
    private String schoolYear;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)", required = true)
    @TableField("department")
    private Integer department;

    @ApiModelProperty(value = "级组ID", required = true)
    @TableField("grade_group")
    private Long gradeGroup;

    @ApiModelProperty(value = "班级ID")
    @TableField("class_id")
    private Long classId;

    @ApiModelProperty(value = "登记人ID")
    @TableField("registrant_id")
    private Long registrantId;

    @ApiModelProperty(value = "登记人")
    @TableField("registrant")
    private String registrant;

    @ApiModelProperty(value = "处理状态：0:待处理；1：处理成功；2：处理失败")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "班级成绩压缩包下载地址")
    @TableField("zip_url")
    private String zipUrl;
}