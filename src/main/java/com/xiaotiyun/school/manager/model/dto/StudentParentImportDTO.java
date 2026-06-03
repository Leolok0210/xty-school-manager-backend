package com.xiaotiyun.school.manager.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class StudentParentImportDTO {


    private Long id;

    /**
     * 学校ID
     */
    private Long schoolId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 家长名称
     */
    private String parentName;

    /**
     * 家长手机号
     */
    private String parentPhone;

    /**
     * 家长与学生关系
     */
    private String parentRelation;

    /**
     * 家长类型:1-父亲关系;2-母亲关系;3-监护人关系;4-其他家长关系
     */
    private String parentType;

    /**
     * '职业
     */
    private String job;



    /**
     * 是否接受短讯
     */
    private Boolean acceptSms;

    /**
     * '任职单位
     */

    private String jobUnit;

    /**
     * '与监护人同住
     */

    private Boolean withGuardian;

    /**
     * '监护人流动电话
     */

    private String guardianMobile;

    /**
     * '地址区域ID
     */

    private String addressAreaId;

    /**
     * '监护人住址
     */

    private String guardianAddress;
}
