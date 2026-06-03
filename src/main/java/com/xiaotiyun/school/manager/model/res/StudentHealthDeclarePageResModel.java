package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("学生健康申报分页查询结果")
public class StudentHealthDeclarePageResModel {

    /**
     * 健康申报ID
     */
    private Long id;

    /**
     * 学年
     */
    private String schoolYear;

    /**
     * 提交时间
     */
    private LocalDateTime submitTime;
}
