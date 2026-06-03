package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 部门响应参数类
 */
@Data
@ApiModel(description = "部门响应参数")
public class DeptResModel {
    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 学校ID
     */
    @ApiModelProperty(value = "学校ID")
    private Long schoolId;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String name;

    /**
     * 父部门ID
     */
    @ApiModelProperty(value = "父部门ID")
    private Long parentId;

    /**
     * 层级，最多20级
     */
    @ApiModelProperty(value = "层级，最多20级")
    private Integer level;

    /**
     * 主管ID
     */
    @ApiModelProperty(value = "主管ID,教师ID")
    private Long managerId;

    /**
     * 主管姓名
     */
    @ApiModelProperty(value = "主管姓名")
    private String managerName;

    /**
     * 人数
     */
    @ApiModelProperty(value = "人数")
    private Integer userCount;

    /**
     * 子部门
     */
    @ApiModelProperty(value = "子部门列表")
    private List<DeptResModel> children;

    /**
     * 部门人员列表
     */
    @ApiModelProperty(value = "部门人员列表")
    private List<UserSchoolRelResModel> userList;
}