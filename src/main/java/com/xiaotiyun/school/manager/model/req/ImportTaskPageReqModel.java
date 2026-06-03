package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.common.PageReqModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ImportTaskPageReqModel extends PageReqModel {
    @NotNull(message = "学校ID不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @ApiModelProperty("类型（1.学生资料；2.学生照片；3.科目资料；4.班级资料；5.学生素质成绩；6.学生考勤；7.教师考勤；8.用户信息；9.义工服务；10.活动匹配；" +
            "11.余暇活动成绩；12.余暇活动课程；13.学生医护保健记录；14.学生奖励；15.学生惩罚；16.常规表现）")
    private List<Integer> types;
}