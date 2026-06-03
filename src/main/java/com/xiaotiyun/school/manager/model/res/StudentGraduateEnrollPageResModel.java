package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentGraduateEnrollPageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("级组名称")
    private String gradeName;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("类型(1.留级；2.毕业)")
    private Integer type;
    @ApiModelProperty("毕业类型(1.升学；2.就业)")
    private Integer graduateType;
    @ApiModelProperty("职业")
    private String job;
    @ApiModelProperty("就读地点")
    private String schoolAddress;
    @ApiModelProperty("就读院校")
    private String faculty;
    @ApiModelProperty("就读科系")
    private String department;
}