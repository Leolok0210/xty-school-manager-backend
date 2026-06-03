package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student_info")
public class StudentEntity extends BaseEntity {
    /**
     * 学校id
     */
    private Long schoolId;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 中文姓名
     */
    private String chineseName;

    /**
     * 学生照片
     */
    private String imgUrl;

    /**
     * 外文姓名
     */
    private String englishName;

    /**
     * 成绩展示姓名类型(1:中文姓名,2:外文姓名)
     */
    private Integer displayNameType;

    /**
     * 状态(1:在校,2:毕业,3:退学,4:休学,5:转学)
     */
    private Integer status;

    /**
     * 学生编号
     */
    private String studentNo;

    /**
     * 学生证编号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String educationNo;

    /**
     * 座位号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer seatNo;

    /**
     * 性别(1:男,2:女)
     */
    private Integer gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 出生地
     */
    private String birthPlace;

    /**
     * 国籍
     */
    private String nationality;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 证件类型(1:身份证,2:护照,4:港澳台居民居住证,5:外国人永久居留身份证)
     */
    private Integer idType;

    /**
     * 证件编号
     */
    private String idNo;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 回乡证编号
     */
    private String reEntryPermitNo;

    /**
     * 证件发出地点
     */
    private String idIssuePlace;

    /**
     * 证件发出日期
     */
    private LocalDate idIssueDate;

    /**
     * 证件有效日期
     */
    private LocalDate idValidDate;

    /**
     * 逗留许可证类型(1:永久,2:有限期,3:其他)
     */
    private Integer stayType;

    /**
     * 逗留许可发出日期
     */
    private LocalDate stayIssueDate;

    /**
     * 逗留许可有效日期
     */
    private LocalDate stayValidDate;

    /**
     * 常住地址区域id
     */
    private String permanentAddressAreaId;

    /**
     * 常住地址
     */
    private String permanentAddress;

    /**
     * 夜间留宿地址区域id
     */
    private String nightAddressAreaId;

    /**
     * 夜间留宿地址
     */
    private String nightAddress;

    /**
     * 常住住址电话
     */
    private String permanentPhone;

    /**
     * 夜间留宿住址电话
     */
    private String nightPhone;

    /**
     * 手提电话
     */
    private String mobilePhone;

    /**
     * 其他联络电话
     */
    private String otherPhone;

    /**
     * 疫苗接种证明(1:已提交并完成,2:已提交未完成,3:未提交)
     */
    private Integer vaccineStatus;

    /**
     * 如遇意外之送往医院
     */
    private Long emergencyHospital;

    /**
     * 紧急医院卡ID
     */
    private String emergencyHospitalCardId;

    /**
     * 金卡号码
     */
    private String goldCardNo;

    /**
     * 文理科(1文科 2理科) 理工商科(1理工科 2理科 3商科)
     */
    private Integer artsScience;

    /**
     * 参加意向,1-可以参加;2-有限参加;3-暂停申报
     */
    private Integer intention;

    /**
     * 意向证明照片
     */
    private String proveImgUrl;

    /**
     * 是否申报健康,0-未申报,1-已申报
     */
    private Integer isHealthDeclared;
} 