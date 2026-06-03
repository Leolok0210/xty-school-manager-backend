package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("欠交作业表现详情返回信息")
public class MissingAssignmentPerformanceDetailResModel {
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("所属学年")
    private String sid;

    @ApiModelProperty("学段name")
    private String termName;

    @ApiModelProperty("学期")
    private Long term;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    //座位号
    @ApiModelProperty("座位号")
    private Integer seatNo;

    @ApiModelProperty("班级name")
    private String className;

    @ApiModelProperty("班级编号")
    private Integer classNumber;

    @ApiModelProperty("日期")
    private LocalDateTime date;

    @ApiModelProperty("科目")
    private Long subjectId;
    @ApiModelProperty("科目name")
    private String subjectName;

    @ApiModelProperty("作业描述")
    private String assignmentDescription;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户name")
    private String userName;

    @ApiModelProperty("组级name")
    public String gradeGroupName;

}