package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StudentQualityScoreListResModel {

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("name")
    private String studentName;

    @ApiModelProperty("英文名")
    private String englishName;

    //座位号
    @ApiModelProperty("座位号")
    private Integer seatNo;


    private List<StudentQualityScoreDetailResModel> resModels;
}
