package com.xiaotiyun.school.manager.model.excel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生信息")
public class StudentExportDTO {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("中文姓名")
    private String chineseName;
    @ApiModelProperty("外文姓名")
    private String englishName;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("性别")
    private Integer gender;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("緊急聯絡人聯絡電話")
    private String emergencyPhone;
}