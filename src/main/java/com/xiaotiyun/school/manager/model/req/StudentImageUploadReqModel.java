package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class StudentImageUploadReqModel {
    @NotNull(message = "学校id不能为空")
    @ApiModelProperty(value = "学校id", required = true)
    private Long schoolId;
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;
    @NotNull(message = "图片文件不能为空")
    @ApiModelProperty(value = "图片文件", required = true)
    private MultipartFile file;
}
