package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任教设置查询请求信息")
public class TeachingSettingQueryReqModel {
    @ApiModelProperty(value = "学年",required = true)
    private String sid;

    @ApiModelProperty(value = "学校ID",required = true)
    private Long schoolId;

    @ApiModelProperty("班级ID")
    private Long classId;

//    @ApiModelProperty("科目ID")
    private Long subjectId;

    @ApiModelProperty("科目名称")
    private String subjectName;

//    @ApiModelProperty("任教老师ID")
    private Long teacherId;

    @ApiModelProperty("老师名称")
    private String teacherName;

    @ApiModelProperty(value = "页码",required = true)
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小",required = true)
    private Integer pageSize;
}