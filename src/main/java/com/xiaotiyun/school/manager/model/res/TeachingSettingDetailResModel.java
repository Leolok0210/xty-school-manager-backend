package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("任教设置详情返回信息")
public class TeachingSettingDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("学年")
    private String sid;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("班级ID")
    private Long classId;

    @ApiModelProperty("科目ID")
    private Long subjectId;

    @ApiModelProperty("任教老师ID")
    private Long teacherId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("科目名称")
    private String subjectName;

    @ApiModelProperty("任教老师名称")
    private String teacherName;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("组级名称")
    private String gradeGroupName;

    @ApiModelProperty("教师手机号")
    private String teacherPhoneNumber;
}