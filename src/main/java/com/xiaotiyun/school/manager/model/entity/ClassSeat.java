package com.xiaotiyun.school.manager.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaotiyun.school.manager.basic.common.BaseEntity;
import lombok.Data;

@Data
@TableName("class_seat")
public class ClassSeat extends BaseEntity {
    /**
     * 所属学年
     */
    private String sid; // 修改: Long 改为 String

    /**
     * 班级id
     */
    private Long classId;

    /**
     * 座位号
     */
    private Integer seatNumber;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 学校ID
     */
    private Long schoolId;
}