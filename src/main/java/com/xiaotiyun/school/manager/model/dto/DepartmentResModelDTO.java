package com.xiaotiyun.school.manager.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel(description = "部门信息")
public class DepartmentResModelDTO {
    @ApiModelProperty(value = "部门名称")
    private String name;

    @ApiModelProperty(value = "父亲部门id,根部门该项为0")
    private Integer parentid;

    @ApiModelProperty(value = "部门id,根部门固定为1")
    private Integer id;

    @ApiModelProperty(value = "部门类型，32位整型，1表示班级，2表示年级，3表示学段，4表示校区，5表示学校（根部门）")
    private Integer type;

    @ApiModelProperty(value = "部门管理员列表")
    private List<DepartmentAdminDTO> department_admins;

    @ApiModelProperty(value = "是否开启班级群，1表示开启，0表示关闭。仅部门类型为班级时才返回该字段")
    private Integer open_group_chat;

    @ApiModelProperty(value = "班级群id。仅部门类型为班级时且open_group_chat为1时才返回该字段")
    private String group_chat_id;

    @ApiModelProperty(value = "是否毕业，1表示已毕业，0表示未毕业。仅部门类型为班级时才返回该字段")
    private Integer is_graduated;
}
