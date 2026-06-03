package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class StudentPromotionPageReqModel extends PageReqModel {
    @ApiModelProperty(value = "学年")
    private String schoolYear;
    @ApiModelProperty(value = "班级id")
    private Long classId;
    @ApiModelProperty(value = "学生id")
    private Long studentId;
    @ApiModelProperty(value = "类型（1-升级 2-留级 3-带科）")
    private Integer promotionType;

    private Long userId;
}