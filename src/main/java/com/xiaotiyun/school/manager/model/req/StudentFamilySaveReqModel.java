package com.xiaotiyun.school.manager.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel("学生家庭信息保存参数")
public class StudentFamilySaveReqModel {
    
    // 父亲信息
    @ApiModelProperty("父亲姓名")
    private String fatherName;
    
    @ApiModelProperty("父亲联络电话")
    private String fatherPhone;
    
    @ApiModelProperty("父亲是否接受短信(0:否,1:是)")
    private Integer fatherSms;
    
    @ApiModelProperty("父亲职业")
    private String fatherOccupation;
    
    @ApiModelProperty("父亲任职单位")
    private String fatherCompany;
    
    // 母亲信息
    @ApiModelProperty("母亲姓名")
    private String motherName;
    
    @ApiModelProperty("母亲联络电话")
    private String motherPhone;
    
    @ApiModelProperty("母亲是否接受短信(0:否,1:是)")
    private Integer motherSms;
    
    @ApiModelProperty("母亲职业")
    private String motherOccupation;
    
    @ApiModelProperty("母亲任职单位")
    private String motherCompany;
    
    // 监护人信息
    @ApiModelProperty("监护人姓名")
    private String guardianName;
    
    @ApiModelProperty("监护人联络电话")
    private String guardianPhone;
    
    @ApiModelProperty("监护人是否接受短信(0:否,1:是)")
    private Integer guardianSms;
    
    @ApiModelProperty("监护人职业")
    private String guardianOccupation;
    
    @ApiModelProperty("监护人任职单位")
    private String guardianCompany;
    
    @ApiModelProperty("监护人关系(1:父亲,2:母亲,3:祖父,4:祖母,5:其他;6:外公,7:外婆)")
    private Integer guardianRelation;
    
    @ApiModelProperty("是否与监护人同住(0:否,1:是)")
    private Integer liveWithGuardian;
    
    @ApiModelProperty("监护人流动电话")
    private String guardianMobile;
    
    @ApiModelProperty("监护人住址区域id")
    private String guardianAddressAreaId;

    @ApiModelProperty("监护人住址")
    private String guardianAddress;

    // 紧急联系人信息
    @ApiModelProperty(value = "紧急联络人姓名", required = true)
    @NotBlank(message = "紧急联络人姓名不能为空")
    private String emergencyContact;
    
    @ApiModelProperty("紧急联络人与学生关系(1:父亲,2:母亲,3:祖父,4:祖母,5:其他;6:外公,7:外婆)")
    private Integer emergencyRelation;
    
    @ApiModelProperty(value = "紧急联络人联络电话", required = true)
    @NotBlank(message = "紧急联络人联络电话不能为空")
    private String emergencyPhone;
    
    @ApiModelProperty("紧急联络人住址区域id")
    private String emergencyAddressAreaId;

    @ApiModelProperty("紧急联络人住址")
    private String emergencyAddress;

    // 第二紧急联系人信息
    @ApiModelProperty(value = "第二紧急联络人姓名")
    private String secondEmergencyContact;

    @ApiModelProperty("第二紧急联络人与学生关系(1:父亲,2:母亲,3:祖父,4:祖母,5:其他;6:外公,7:外婆)")
    private Integer secondEmergencyRelation;

    @ApiModelProperty(value = "第二紧急联络人联络电话")
    private String secondEmergencyPhone;

    @ApiModelProperty("第二紧急联络人住址区域id")
    private String secondEmergencyAddressAreaId;

    @ApiModelProperty("第二紧急联络人住址")
    private String secondEmergencyAddress;

    @ApiModelProperty("家庭状况")
    private String familyStatus;

    @ApiModelProperty("备注")
    private String remark;
}