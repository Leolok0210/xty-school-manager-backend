package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ImportTaskPageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("类型（1.学生资料；2.学生照片；3.科目资料；4.班级资料；5.学生素质成绩；6.学生考勤；7.教师考勤；8.用户信息；9.义工服务；10.活动匹配；" +
            "11.余暇活动成绩；12.余暇活动课程；13.学生医护保健记录；14.学生奖励；15.学生惩罚；16.常规表现）")
    private Integer type;
    @ApiModelProperty("总记录数")
    private Integer totalCount;
    @ApiModelProperty("成功记录数")
    private Integer successCount;
    @ApiModelProperty("失败记录数")
    private Integer failCount;
    @ApiModelProperty("状态(0:待处理,1:处理中,2:已处理)")
    private Integer status;
    @ApiModelProperty("上传时间")
    private LocalDateTime createTime;
}