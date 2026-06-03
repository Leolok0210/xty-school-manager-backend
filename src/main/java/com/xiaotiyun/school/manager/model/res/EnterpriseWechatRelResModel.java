package com.xiaotiyun.school.manager.model.res;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;



@Data
@ApiModel(description = "企业微信关联关系接收对象")
public class EnterpriseWechatRelResModel {

    @ApiModelProperty(value = "id", example = "1", required = true)
    private Long id;



    @ApiModelProperty(value = "关联id", example = "1", required = true)
    private Long relId;

    @ApiModelProperty(value = "关联类型 1-级组 2-班级 3-学生 4-家长 5-学部", example = "2", required = true)
    private Integer type;

    /**
     * 关联企业微信id
     */
    @ApiModelProperty(value = "关联企业微信id", example = "1", required = true)
    private String wxId;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID", example = "1", required = true)
    private Long schoolId;


    @ApiModelProperty(value = "名称 两边名称一样，所以这里返回一个即可", example = "1", required = true)
    private String name;


}
