package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 跨境学生登记返回参数
 */
@Data
@ApiModel("跨境学生登记返回参数")
public class CrossBorderStudentResModel {
    @ApiModelProperty(value = "学生id")
    private Long studentId;

    @ApiModelProperty(value = "入境内地证件类型(1:港澳居民来往内地通行证,2:往来港澳通行证,3:其他)")
    private Integer mainlandCertificateType;

    @ApiModelProperty(value = "入境内地证件类型其他描述")
    private String mainlandCertificateTypeOther;

    @ApiModelProperty(value = "入境内地证件号码")
    private String mainlandCertificateNumber;

    @ApiModelProperty(value = "陪同人中文姓名")
    private String companionChineseName;

    @ApiModelProperty(value = "陪同人外文姓名或译音")
    private String companionForeignName;

    @ApiModelProperty(value = "陪同人性别(1:男,2:女)")
    private Integer companionGender;

    @ApiModelProperty(value = "陪同人出生日期")
    private LocalDate companionBirthDate;

    @ApiModelProperty(value = "陪同人入境澳门证件类型(1:澳门居民身份证,2:香港居民身份证,3:往来港澳通行证,4:其他)")
    private Integer macauCertificateType;

    @ApiModelProperty(value = "陪同人入境澳门证件类型其他描述")
    private String macauCertificateTypeOther;

    @ApiModelProperty(value = "陪同人往来港澳通行证签注种类(1:T,2:G,3:S,4:D,5:L,6:Q)")
    private Integer hkMacauPassVisaType;

    @ApiModelProperty(value = "陪同人入境澳门证件号码")
    private String macauCertificateNumber;

    @ApiModelProperty(value = "陪同人入境内地证件类型(1:港澳居民来往内地通行证,2:往来港澳通行证,3:其他)")
    private Integer mainlandEntryCertificateType;

    @ApiModelProperty(value = "陪同人入境内地证件类型其他描述")
    private String mainlandEntryCertificateTypeOther;

    @ApiModelProperty(value = "陪同人入境内地证件号码")
    private String mainlandEntryCertificateNumber;
}