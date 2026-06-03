package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel("学生保存参数")
public class StudentResModel {
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "学校id")
    private Long schoolId;
    @ApiModelProperty(value = "学校名称")
    private String schoolName;
    @ApiModelProperty("学校配置")
    private StudentSchoolConfigResModel schoolConfig;
    @ApiModelProperty(value = "学生照片")
    private String imgUrl;
    @ApiModelProperty(value = "中文姓名")
    private String chineseName;
    @ApiModelProperty("外文姓名")
    private String englishName;
    @ApiModelProperty("成绩展示姓名类型(1:中文姓名,2:外文姓名)")
    private Integer displayNameType;
    @ApiModelProperty(value = "状态(1:在校,2:毕业,3:退学,4:休学,5:转学)")
    private Integer status;
    @ApiModelProperty(value = "学生编号")
    private String studentNo;
    @ApiModelProperty("教青局编号")
    private String educationNo;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty(value = "班级ID")
    private Long classId;
    @ApiModelProperty(value = "班级名称")
    private String className;
    @ApiModelProperty(value = "级组ID")
    private Long groupId;
    @ApiModelProperty(value = "级组名称")
    private String groupName;
    @ApiModelProperty(value = "性别(1:男,2:女)")
    private Integer gender;
    @ApiModelProperty("出生日期")
    private LocalDate birthDate;
    @ApiModelProperty("出生地")
    private String birthPlace;
    @ApiModelProperty("国籍")
    private String nationality;
    @ApiModelProperty("籍贯")
    private String nativePlace;
    @ApiModelProperty("证件类型(1:澳门居民身份证,2:护照,4:港澳台居民居住证,5:外国人永久居留身份证)")
    private Integer idType;
    @ApiModelProperty("证件编号")
    private String idNo;
    @ApiModelProperty("身份证号")
    private String idCardNo;
    @ApiModelProperty("回乡证编号")
    private String reEntryPermitNo;
    @ApiModelProperty("证件发出地点")
    private String idIssuePlace;
    @ApiModelProperty("证件发出日期")
    private LocalDate idIssueDate;
    @ApiModelProperty("证件有效日期")
    private LocalDate idValidDate;
    @ApiModelProperty("逗留许可证类型(1:永久,2:有限期,3:其他)")
    private Integer stayType;
    @ApiModelProperty("逗留许可发出日期")
    private LocalDate stayIssueDate;
    @ApiModelProperty("逗留许可有效日期")
    private LocalDate stayValidDate;
    @ApiModelProperty("常住地址区域id")
    private String permanentAddressAreaId;
    @ApiModelProperty("常住地址")
    private String permanentAddress;
    @ApiModelProperty("夜间留宿地址区域id")
    private String nightAddressAreaId;
    @ApiModelProperty("夜间留宿地址")
    private String nightAddress;
    @ApiModelProperty("常住住址电话")
    private String permanentPhone;
    @ApiModelProperty("夜间留宿住址电话")
    private String nightPhone;
    @ApiModelProperty("手提电话")
    private String mobilePhone;
    @ApiModelProperty("其他联络电话")
    private String otherPhone;
    @ApiModelProperty("疫苗接种证明(1:已提交并完成,2:已提交未完成,3:未提交)")
    private Integer vaccineStatus;
    @ApiModelProperty("如遇意外之送往医院")
    private Long emergencyHospital;
    @ApiModelProperty("金卡号码")
    private String goldCardNo;
    @ApiModelProperty("医院医疗卡号码")
    private String emergencyHospitalCardId;
    @ApiModelProperty("文理科(1文科 2理科) 理工商科(1理工科 2理科 3商科)")
    private Integer artsScience;
    @ApiModelProperty("参加意向")
    private Integer intention;
    @ApiModelProperty("意向证明照片")
    private String proveImgUrl;
    @ApiModelProperty("家庭信息")
    private StudentFamilyResModel familyInfo;
}