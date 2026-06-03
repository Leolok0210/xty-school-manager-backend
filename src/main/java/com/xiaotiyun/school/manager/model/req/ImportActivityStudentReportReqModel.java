package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 导入活动匹配请求模型
 */
@Data
@ApiModel("导入活动匹配请求")
public class ImportActivityStudentReportReqModel {

    @NotNull(message = "活动ID不能为空")
    @ApiModelProperty(value = "活动ID", required = true)
    private Long activityId;

    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "课程ID", required = true)
    private Long lensonId;

    @ApiModelProperty(value = "Excel文件", required = true)
    private MultipartFile uploadFile;

    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;

    @ApiModelProperty(value = "忽略", required = false)
    private Long userId;

    @ApiModelProperty(value = "忽略", required = false)
    private String Username;

} 