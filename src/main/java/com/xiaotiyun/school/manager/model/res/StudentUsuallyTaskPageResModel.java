package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("学生平时分登记信息")
public class StudentUsuallyTaskPageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("测验名称")
    private String name;
    @ApiModelProperty("次数")
    private Integer frequency;
    @ApiModelProperty("平时成绩类型id")
    private Long typeId;
    @ApiModelProperty("平时成绩类型名称")
    private String typeName;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("级组名称")
    private String gradeGroupName;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学段名称")
    private String periodName;
    @ApiModelProperty("测验时间")
    private LocalDate testDate;
    @ApiModelProperty("科目名称")
    private String subjectName;
    @ApiModelProperty("参与人数")
    private Integer partakeCount;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新人")
    private String updateUser;
    @ApiModelProperty("备注")
    private String remark;
}