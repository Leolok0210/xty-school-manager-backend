package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StudentSchoolConfigResModel {

    @ApiModelProperty("平时成绩类型是否关联科目,0-不关联,1-关联")
    private int isUsuallyTypeRelSubject;
}
