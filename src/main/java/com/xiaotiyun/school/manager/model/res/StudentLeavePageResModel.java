package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@ApiModel("请假缺席响应数据")
public class StudentLeavePageResModel {

    @ApiModelProperty(value = "记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "学校id")
    private Long schoolId;

    @ApiModelProperty(value = "学年", example = "2023-2024")
    private String schoolYear;

    @ApiModelProperty(value = "班级ID", example = "201")
    private Long classId;

    @ApiModelProperty(value = "级组id")
    private Long gradeId;

    @ApiModelProperty(value = "级组名称")
    private String gradeName;

    @ApiModelProperty(value = "班级名称")
    private String className;

    @ApiModelProperty(value = "学生ID", example = "1001")
    private Long studentId;

    @ApiModelProperty(value = "学生姓名", example = "张三")
    private String studentName;

    @ApiModelProperty(value = "座位号", example = "15")
    private Integer seatNo;

    @ApiModelProperty(value = "请假日期", example = "2023-10-01")
    private LocalDate leaveDate;

    @ApiModelProperty(value = "类型 1-请假 2-缺席", example = "1")
    private Integer leaveType;

    @ApiModelProperty(value = "节数", example = "2")
    private Integer periods;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "记录人ID", example = "1001")
    private Long registrantId;

    @ApiModelProperty(value = "记录人姓名", example = "李四")
    private String registrantName;

    @ApiModelProperty(value = "登记人类型，0-教师，1-学生")
    private Integer registrantType;

    @ApiModelProperty(value = "课节信息")
    private List<StudentLeaveCourseResModel> courses;

    @ApiModelProperty(value = "图片")
    private List<StudentLeaveImageResModel> images;
}