package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("科目详情返回信息")
public class SubjectDetailResModel {
    @ApiModelProperty("科目ID，这个是原始科目表id，不是年级科目表id")
    private Long id;
    
    @ApiModelProperty("科目编号")
    private String subjectNumber;
    
    @ApiModelProperty("科目名称")
    private String subjectName;

    @ApiModelProperty("科目英文名称")
    private String subjectEnglishName;

    @ApiModelProperty("单位")
    private Integer unit;
    
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    // 增加 schoolId 字段
    @ApiModelProperty("学校ID")
    private Long schoolId;

    @ApiModelProperty("范围 格式[1,2,3]")
    private String scope;

}