package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("学生分页查询参数")
public class StudentPageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校id",required = true)
    private Long schoolId;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("级组id")
    private Long gradeId;
    @ApiModelProperty("班级ID")
    private Long classId;
    @ApiModelProperty("学生信息")
    private String studentInfo;
    @ApiModelProperty("状态(1:在校,2:毕业,3:退学,4:休学,5:转学)")
    private Integer status;
    @ApiModelProperty("性别(1:男,2:女)")
    private Integer gender;
    @ApiModelProperty("证件编号")
    private String idNo;
    @ApiModelProperty("学部")
    private String department;
    @ApiModelProperty("常用住址")
    private String permanentAddress;
    @ApiModelProperty("退学年份")
    private String outYear;

    private Long userId;

    private List<Long> classIds;
}