package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class EnterpriseWechatNoticeFilterValueDTO {

    /**
     * 类型(0.全部；1.已报名/已申报/学部；2.未报名/未申报/年级；3.已录取/班级；4.未录取)
     */
    private Integer type;
    /**
     * 学部(1.幼稚园；2.小学；3.中学)
     */
    private Integer department;
    /**
     * 级组id
     */
    private Long gradeId;
    /**
     * 班级id
     */
    private Long classId;
}
