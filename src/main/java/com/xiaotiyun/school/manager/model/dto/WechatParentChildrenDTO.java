package com.xiaotiyun.school.manager.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WechatParentChildrenDTO {
    /**
     * 学生的UserID
     * 是必填字段。
     */
    private String student_userid;

    /**
     * 家长与学生的关系，最长32字节
     * 是必填字段。
     */
    private String relation;
}
