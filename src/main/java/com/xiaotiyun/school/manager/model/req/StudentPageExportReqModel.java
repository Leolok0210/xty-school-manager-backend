package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("学生分页查询参数")
public class StudentPageExportReqModel extends StudentPageReqModel {
    @ApiModelProperty(value = "表头json字段【json格式为[{'module':'basic(模块:basic基础数据、family家庭情况、enrollment入学记录、medicalNotice医护注意事项、crossBorder跨境学生、dateRecord日期记录)','headers':[{'name':'学生姓名(excel表头名称)','field':'chineseName(数据字段)'}]}]】(表头包括必选字段)", required = true)
    @NotBlank(message = "表头不能为空")
    private String headerStr;
}