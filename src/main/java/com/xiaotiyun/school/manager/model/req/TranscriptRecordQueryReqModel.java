package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "学年成绩单列表请求类")
public class TranscriptRecordQueryReqModel {
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "学年", required = true)
    private String schoolYear;

    @ApiModelProperty(value = "学部(1:幼稚园 2:小学 3:中学)")
    private Integer department;

    @ApiModelProperty(value = "级组ID")
    private Long gradeGroup;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;

    private Long userId;

    private List<Long> classIds;
}