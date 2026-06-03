package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;

@Data
public class SynWxChatStatusUpdateDTO {

    private long relId;

    /**
     * 批量操作的时候传入
     */
    private String thirdId;

    private String phone;
}
