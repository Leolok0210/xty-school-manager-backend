package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class StudentInfoResModelDTO {
    @ApiModelProperty(value = "学生UserID")
    private String student_userid;

    @ApiModelProperty(value = "学生名字")
    private String name;

    @ApiModelProperty(value = "学生所在的班级id列表,不超过20个")
    private List<Integer> department;

    @ApiModelProperty(value = "学生家长列表")
    private List<StudentParentResModelDTO> parents;
}
