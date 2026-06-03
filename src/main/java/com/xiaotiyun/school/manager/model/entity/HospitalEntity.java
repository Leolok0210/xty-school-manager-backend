package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_hospital")
public class HospitalEntity extends BaseEntity {
    /**
     * 医院名称
     */
    private String name;

    /**
     * 医院电话
     */
    private String phone;

    /**
     * 医院地址
     */
    private String address;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 学校ID
     */
    private Long schoolId;
} 