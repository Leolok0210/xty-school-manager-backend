package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserRewardDetailsDTO {
    private int number;

    private LocalDate date;

    //计算出的
    private int reportNumber;
    
    /**
     * 惩罚类型，具体含义：
     * 1. 大过
     * 2. 小过
     * 3. 缺点
     */
    private String penaltyType;
}
