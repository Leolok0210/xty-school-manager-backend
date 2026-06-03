package com.xiaotiyun.school.manager.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("学生信息")
public class StudentPageResModel {
    @ApiModelProperty("ID")
    private Long id;
    @ApiModelProperty("学年")
    private String schoolYear;
    @ApiModelProperty("学生照片")
    private String imgUrl;
    @ApiModelProperty("学生姓名")
    private String studentName;
    @ApiModelProperty("性别(1:男,2:女)")
    private Integer gender;
    @ApiModelProperty("学生编号")
    private String studentNo;
    @ApiModelProperty("座位号")
    private Integer seatNo;
    @ApiModelProperty("班级ID")
    private Long classId;
    @ApiModelProperty("班级名称")
    private String className;
    @ApiModelProperty("级组")
    private String gradeName;
    @ApiModelProperty("状态(1:在校,2:毕业,3:退学,4:休学,5:转学)")
    private Integer status;
    @ApiModelProperty("英文名")
    private String englishName;
    @ApiModelProperty("教青局编号")
    private String educationNo;
    @ApiModelProperty("国籍")
    private String nationality;
    @ApiModelProperty("籍贯")
    private String nativePlace;
    @ApiModelProperty("证件类型(1:澳门居民身份证,2:护照,4:港澳台居民居住证,5:外国人永久居留身份证)")
    private Integer idType;
    @ApiModelProperty("证件编号")
    private String idNo;
    @ApiModelProperty("常住地址")
    private String permanentAddress;
    @ApiModelProperty("手提电话")
    private String mobilePhone;
    @ApiModelProperty("常住住址电话")
    private String permanentPhone;
    @ApiModelProperty("退学时间")
    private String outTime;
    @ApiModelProperty("退学原因")
    private String outReason;
    @ApiModelProperty("升留级情况")
    private String escalationSituation;
    @ApiModelProperty("学部")
    private Integer department;
    @ApiModelProperty("文科理科：0-未选，1-文科，2-理科")
    private Integer artsScience;
    @ApiModelProperty("参加意向")
    private Integer intention;
    @ApiModelProperty("意向证明照片")
    private String proveImgUrl;
    @ApiModelProperty("是否申报健康")
    private Boolean isHealthDeclared;
}