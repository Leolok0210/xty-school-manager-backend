package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 跨境学生登记请求参数
 */
@Data
@ApiModel("跨境学生登记请求参数")
public class CrossBorderStudentReqModel {
    @NotNull(message = "学生id不能为空")
    @ApiModelProperty(value = "学生id", required = true)
    private Long studentId;

    @ApiModelProperty(value = "入境内地证件类型(1:港澳居民来往内地通行证（回乡证）,2:往来港澳通行证（通行证）,3:其他)")
    private Integer mainlandCertificateType;

    @Size(max = 100, message = "入境内地证件类型其他描述不能超过100字")
    @ApiModelProperty(value = "入境内地证件类型其他描述(当类型为其他时必填)")
    private String mainlandCertificateTypeOther;

    @Size(max = 50, message = "入境内地证件号码不能超过50字")
    @ApiModelProperty(value = "入境内地证件号码")
    private String mainlandCertificateNumber;

    @Size(max = 50, message = "陪同人中文姓名不能超过50字")
    @ApiModelProperty(value = "陪同人中文姓名")
    private String companionChineseName;

    @Size(max = 50, message = "陪同人外文姓名或译音不能超过50字")
    @ApiModelProperty(value = "陪同人外文姓名或译音")
    private String companionForeignName;

    @ApiModelProperty(value = "陪同人性别(1:男,2:女)")
    private Integer companionGender;

    @ApiModelProperty(value = "陪同人出生日期")
    private LocalDate companionBirthDate;

    @ApiModelProperty(value = "陪同人入境澳门证件类型(1:澳门居民身份证,2:香港居民身份证,3:往来港澳通行证,4:其他)")
    private Integer macauCertificateType;

    @Size(max = 100, message = "陪同人入境澳门证件类型其他描述不能超过100字")
    @ApiModelProperty(value = "陪同人入境澳门证件类型其他描述(当类型为其他时必填)")
    private String macauCertificateTypeOther;

    @ApiModelProperty(value = "陪同人往来港澳通行证签注种类(1:探视签注（T）,2:个人旅游签注（G）,3:商务签注（S）,4:逗留签注（D）,5:团体旅游签注（L）,6:其他签注（Q）)")
    private Integer hkMacauPassVisaType;

    @Size(max = 50, message = "陪同人入境澳门证件号码不能超过50字")
    @ApiModelProperty(value = "陪同人入境澳门证件号码")
    private String macauCertificateNumber;

    @ApiModelProperty(value = "陪同人入境内地证件类型(1:港澳居民来往内地通行证,2:往来港澳通行证,3:其他)")
    private Integer mainlandEntryCertificateType;

    @Size(max = 100, message = "陪同人入境内地证件类型其他描述不能超过100字")
    @ApiModelProperty(value = "陪同人入境内地证件类型其他描述(当类型为其他时必填)")
    private String mainlandEntryCertificateTypeOther;

    @Size(max = 50, message = "陪同人入境内地证件号码不能超过50字")
    @ApiModelProperty(value = "陪同人入境内地证件号码")
    private String mainlandEntryCertificateNumber;
}