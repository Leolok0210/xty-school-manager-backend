package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentListResModel {
    private Long id;
    @ApiModelProperty(value = "中文姓名")
    private String chineseName;
    @ApiModelProperty("外文姓名")
    private String englishName;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("学生编号")
    private String studentNo;
}
