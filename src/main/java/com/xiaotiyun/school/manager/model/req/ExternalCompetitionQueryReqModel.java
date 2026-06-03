package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("校外比赛查询请求")
public class ExternalCompetitionQueryReqModel extends PageReqModel {
    @ApiModelProperty("学校id")
    @NotNull(message = "学校id不能为空")
    private Long schoolId;

    @ApiModelProperty("学年")
    private String schoolYear;

    @ApiModelProperty("班级id")
    private Long classId;

    @ApiModelProperty("学生id")
    private Long studentId;

    @ApiModelProperty("比赛项目名称")
    private String name;

    @ApiModelProperty("主办单位")
    private String organizer;

    @ApiModelProperty("组别")
    private String groupType;

    @ApiModelProperty("地区")
    private String area;
}