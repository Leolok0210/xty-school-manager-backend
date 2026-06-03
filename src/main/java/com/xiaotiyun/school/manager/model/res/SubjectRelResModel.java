package com.xiaotiyun.school.manager.model.res;

import com.xiaotiyun.school.manager.model.res.SubjectDetailResModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("科目关联返回对象")
public class SubjectRelResModel {
    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("级组id")
    private Long groupId;

    @ApiModelProperty("科目id 和id一样，为了兼容之前的代码这里就不做删除")
    private Long subjectId;

    @ApiModelProperty("序号")
    private Integer number;

    @ApiModelProperty("是否计入平均分 (0. 否，1. 是)")
    private Integer countedInAverage;

    @ApiModelProperty("文科理科：0-公共，1-文科，2-理科 3-商科")
    private Integer artsScience;

    @ApiModelProperty("1-选修 2-必修")
    private Integer subjectType;

    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("成绩展示规则，0-分数，1-评级")
    private Integer showRule;

    @ApiModelProperty("科目信息")
    private SubjectDetailResModel subject;
} 