package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StudentGraduateEnrollPageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("班级id")
    private Long classId;
    @ApiModelProperty("学生信息")
    private String studentInfo;
    @ApiModelProperty("类型(1.留级；2.毕业)")
    private Integer type;
    @ApiModelProperty("毕业类型(1.升学；2.就业)")
    private Integer graduateType;
    @ApiModelProperty("就读地点")
    private String schoolAddress;

    private Long userId;

    private List<Long> classIds;
}