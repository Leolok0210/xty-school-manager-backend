package com.xiaotiyun.school.manager.model.req;

import com.xiaotiyun.school.manager.basic.constant.LanguageConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@ApiModel("新增用户请求")
public class UserAddReqModel {
    @ApiModelProperty(value = "手机号区号")
    private String mobileHead;

    @Pattern(regexp = "^(1[3-9]\\d{9}|\\d{8})$", message = "手机号格式不正确")
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @NotBlank(message = "用户名称不能为空")
    @ApiModelProperty(value = "用户名称", required = true)
    private String username;

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,20}$", message = "用户名格式不正确，格式：8-20个字符，必须包含字母+数字")
    @ApiModelProperty(value = "用户名", required = true)
    private String loginName;

    @NotBlank(message = "用户编号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "用户编号格式不正确，格式：1-20个字符，必须包含字母+数字")
    @ApiModelProperty(value = "用户编号", required = true)
    private String userNumber;

    @NotNull(message = "用户类型不能为空")
    @ApiModelProperty(value = "用户类型(1:教师 2:职员 3:专职人员 4:工友)", required = true)
    private Integer userType;

//    @NotBlank(message = "职务不能为空")
//    @ApiModelProperty(value = "职务", required = true)
//    private String position;

    @NotNull(message = "性别不能为空")
    @ApiModelProperty(value = "性别(1:男 2:女)", required = true)
    private Integer gender;

//    @NotNull(message = "状态不能为空")
//    @ApiModelProperty(value = "状态(1:在职 2:离职)", required = true)
//    private Integer status;

    @NotEmpty(message = "用户组ID列表不能为空")
    @ApiModelProperty(value = "用户组ID列表", required = true)
    private List<Long> userGroupIds;

    @NotNull(message = LanguageConstants.PARAM_ERROR)
    @ApiModelProperty(value = "所属部门", required = true)
    private UserDeptAddReqModel masterDept;

    @ApiModelProperty(value = "兼职部门ID列表")
    private List<UserDeptAddReqModel> slaveDeptList;

    @ApiModelProperty(value = "班级权限")
    private List<UserClassRelReqModel> relList;

    /**
     * 是否需要重置密码 0-不需要 1-需要
     */
    @ApiModelProperty(value = "是否需要重置密码 0-不需要 1-需要", required = true)
    private Integer needResetPwd;
}